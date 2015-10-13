/*
* Copyright 2015 MICRORISC s.r.o.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

#include <string.h>
#include "AUTONETWORK.h"
#include "DPA_LIBRARY.h"
#include "AUTONETWORK_Example.h"

//-----------------
// Global variables
//-----------------
T_PREBONDING_INFO prebonding;
T_AN_STATE autonetworkState;
T_AN_PARAMS ANparams;
T_DPA_PACKET myDpaRequest;
T_DPA_PACKET *myDpaResponse;
T_NETWORK_INFO networkInfo;
unsigned char dpaStep;
volatile unsigned short dpaTimeoutMS = 0;
volatile unsigned long delayTime = 0;
volatile unsigned char terminate = 0;
autonetworkNotify_CB notify_cb = NULL;
autonetworkCheckInput_CB checkInput_cb = NULL;

//-------------------------------------------------
// Get the value of specified bit in bitField array
//-------------------------------------------------
unsigned char getBitValue(unsigned char *bitField, unsigned char bitAddr)
{
   return((bitField[bitAddr / 8] >> (bitAddr % 8)) & 0x01);
}

//----------------------------------------
// Set the specified bit in bitField array
//----------------------------------------
void setBitValue(unsigned char *bitField, unsigned char bitAddr)
{
   bitField[bitAddr / 8] |= (1 << (bitAddr % 8));
}

//-----------------------------------
// Check the specified node is bonded
//-----------------------------------
unsigned char isNodeBonded(unsigned char addr)
{
    return(getBitValue(networkInfo.bondedNodesMap, addr));
}

//---------------------------------------
// Get next free address to bond new node
//---------------------------------------
unsigned char nextFreeAddr(unsigned char from)
{
    unsigned char origAddr = from;

    for( ; ; )
    {
        if(++from > DPA_ADDRESS_MAX)
            from = 1;

        if(isNodeBonded(from) == 0)
            return from;

        // No free address
        if(origAddr == from)
            return(ERR_NO_FREE_ADDRESS);
    }
}

//----------------------------------------------
// The function providing timing for autonetwork
//----------------------------------------------
void autonetworkTick(void)
{
    // DPA communication timeout
    if((dpaStep == CONFIRMATION_WAITING) || (dpaStep == RESPONSE_WAITING))
    {
        if(dpaTimeoutMS)
        {
            if((--dpaTimeoutMS) == 0)
            {
                dpaStep |= DPA_TIMEOUT;
            }
        }
    }

    // Delay function
    if(delayTime)
        delayTime--;
}

//---------------
// Delay function
//---------------
void delayMS(unsigned long ms)
{
    if(ms != 0)
    {
        delayTime = ms;
        while(delayTime)
        {
            // Check input from user application
            if(checkInput_cb != NULL)
            {
                // Stop autonetwork command ?
                if(checkInput_cb(NULL) == CMD_STOP_AN)
                {
                    // Yes, terminate autonetwork
                    terminate = ERR_PROCESS_TERMINATED;
                    return;
                }
            }
        }
    }
}

//------------------------
// Notify main application
//------------------------
void notifyMainApp(unsigned char eventCode, unsigned char param)
{
    if(notify_cb != NULL)
    {
      if(param == EVT_WITHOUT_PARAM)
          notify_cb(eventCode, NULL);
      else
          notify_cb(eventCode, &autonetworkState);
    }
}

//------------------------------
// DPA handler callback function
//------------------------------
void MyDpaAnswerHandler(T_DPA_PACKET *dpaAnswerPkt)
{
    // Waiting for DPA confirmation ?
    if (dpaStep == CONFIRMATION_WAITING && dpaAnswerPkt->Extension.Confirmation.StatusConfirmation == STATUS_CONFIRMATION)
    {
        // Yes, broadcast address ?
        if(myDpaRequest.NAdr != DPA_ADDRESS_BROADCAST)
        {
            // No, wait for DPA response
            dpaStep = RESPONSE_WAITING;
            dpaTimeoutMS = (unsigned short)(dpaAnswerPkt->Extension.Confirmation.Hops + 1) * (unsigned short)(dpaAnswerPkt->Extension.Confirmation.TimeSlotLength) * 10;
            dpaTimeoutMS += ((unsigned short)(dpaAnswerPkt->Extension.Confirmation.HopsResponse) * 50 + 60);
        }
        else
        {
            // Yes, don't wait for DPA response
            dpaStep = DPA_OK;
            myDpaResponse = dpaAnswerPkt;
            dpaTimeoutMS = 0;
            // Wait before next DPA request
            delayMS((unsigned long)(networkInfo.bondedNodesCount) * 40 + 100);
        }        
        return;
    }

    // Waiting for DPA response ?
    if (dpaStep == RESPONSE_WAITING)
    {
        // Yes
        if(dpaAnswerPkt->Extension.Response.ResponseCode == STATUS_NO_ERROR)
            dpaStep = DPA_OK;       // OK
        else
            dpaStep = DPA_ERROR;    // An error

        myDpaResponse = dpaAnswerPkt;
        dpaTimeoutMS = 0;

        // Node addressed ?
        if(dpaAnswerPkt->NAdr != DPA_ADDRESS_COORD)
        {
            // Yes, wait before next DPA request
            delayMS((unsigned long)(networkInfo.bondedNodesCount) * 40);
        }
        else
            delayMS(100);
        return;
    }
}

//-----------------------------
// Prepare and send DPA request
//-----------------------------
void MyDpaLibRequest(unsigned char len)
{
    if (myDpaRequest.NAdr == DPA_ADDRESS_COORD)
        dpaStep = RESPONSE_WAITING;         // DPA answer would be response
    else
        dpaStep = CONFIRMATION_WAITING;     // DPA answer would be confirmation

    if(dpaTimeoutMS == 0)
        dpaTimeoutMS = 500;                 // Default timeout is 500 ms

    // Send the DPA request, wait for result
    DPA_SendRequest(&myDpaRequest, len);
    while(dpaStep < DPA_OK)
    {
              // Check and read serial data 
#ifdef __UART_INTERFACE__   
        MySerialEvent();
#endif
        DPA_LibraryDriver();                 // DPA library handler
    }
    if(dpaStep & DPA_TIMEOUT)
    {
        // Notify main app. except the temporary address and command CMD_COORDINATOR_DISCOVERY
        if((myDpaRequest.NAdr != DPA_ADDRESS_TEMP) && (myDpaRequest.PCmd != CMD_COORDINATOR_DISCOVERY))
        {
            if((dpaStep & 0x7f) == CONFIRMATION_WAITING)
                notifyMainApp(EVT_DPA_CONFIRMATION_TIMEOUT, EVT_WITHOUT_PARAM);
            else
                notifyMainApp(EVT_DPA_RESPONSE_TIMEOUT, EVT_WITHOUT_PARAM);
        }
    }
}

//---------------------------
// Get addressing information
//---------------------------
unsigned char getAddrInfo(void)
{
    // Get addresing info
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_ADDR_INFO;
    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
    {
        networkInfo.bondedNodesCount = myDpaResponse->Extension.Response.Data[0];
        networkInfo.DID = myDpaResponse->Extension.Response.Data[1];
    }
    return(dpaStep);
}

//---------------------
// Get discovered nodes
//---------------------
unsigned char getDiscoveredNodes(void)
{
    // Get discovered nodes
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_DISCOVERED_DEVICES;
    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
        memcpy(networkInfo.discoveredNodesMap, (void*)myDpaResponse->Extension.Response.Data, NODE_BITMAP_SIZE);
    return(dpaStep);
}

//-----------------
// Get bonded nodes
//-----------------
unsigned char getBondedNodes(void)
{
    // Get bonded nodes
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_BONDED_DEVICES;
    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
        memcpy(networkInfo.bondedNodesMap, (void*)myDpaResponse->Extension.Response.Data, NODE_BITMAP_SIZE);
    return(dpaStep);}

//----------------
// Clear all bonds
//----------------
unsigned char clearAllBond(void)
{
    // Clear all bonds
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_CLEAR_ALL_BONDS;
    MyDpaLibRequest(0);
    return(dpaStep);
}

//-------------------
// Remove bonded node
//-------------------
unsigned char removeBondedNode(unsigned char bondAddr)
{
    // Remove bonded node
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_REMOVE_BOND;
    myDpaRequest.Extension.Plain.Data[0] = bondAddr;
    MyDpaLibRequest(1);
    return(dpaStep);
}

//----------
// Discovery
//----------
unsigned char discovery(unsigned char TXpower, unsigned char maxAddr)
{
    // Discovery
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_DISCOVERY;
    myDpaRequest.Extension.Plain.Data[0] = TXpower;
    myDpaRequest.Extension.Plain.Data[1] = maxAddr;
    dpaTimeoutMS = (unsigned short)(networkInfo.bondedNodesCount) * 40 + 10000;
    MyDpaLibRequest(2);
    if(dpaStep == DPA_OK)
        prebonding.param = myDpaResponse->Extension.Response.Data[0];
    return(dpaStep);
}

//--------------
// Set DPA Param
//--------------
unsigned char setDPAParam(unsigned char param)
{
    // Set DPA param
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_SET_DPAPARAMS;
    myDpaRequest.Extension.Plain.Data[0] = param;
    MyDpaLibRequest(1);
    return(dpaStep);
}

//---------
// Set Hops
//---------
unsigned char setHops(unsigned char reqHops, unsigned char respHops)
{
    // Set hops
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_SET_HOPS;
    myDpaRequest.Extension.Plain.Data[0] = reqHops;
    myDpaRequest.Extension.Plain.Data[1] = respHops;
    MyDpaLibRequest(2);
    return(dpaStep);
}

//------
// Batch
//------
unsigned char sendBatch(unsigned short addr, unsigned char len)
{
    // Send Batch command
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_OS;
    myDpaRequest.PCmd = CMD_OS_BATCH;
    dpaTimeoutMS = (unsigned long)(networkInfo.bondedNodesCount + 1) * 40 + 500;
    MyDpaLibRequest(len);
    return(dpaStep);
}

//-------------------------------
// Read remotely bonded module ID
//-------------------------------
unsigned char readPrebondedMID(unsigned short addr, unsigned long *mid, unsigned short *userData)
{
    // Read prebonded MID from [C] or [N]
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    if(addr == DPA_ADDRESS_COORD)
    {
        // Read MID from coordinator
        myDpaRequest.PNum = PNUM_COORDINATOR;
        myDpaRequest.PCmd = CMD_COORDINATOR_READ_REMOTELY_BONDED_MID;
    }
    else
    {
        // Read MID from node
        myDpaRequest.PNum = PNUM_NODE;
        myDpaRequest.PCmd = CMD_NODE_READ_REMOTELY_BONDED_MID;
    }

    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
    {
        *mid = (unsigned long)myDpaResponse->Extension.Response.Data[0];
        *mid |= ((unsigned long)myDpaResponse->Extension.Response.Data[1] << 8);
        *mid |= ((unsigned long)myDpaResponse->Extension.Response.Data[2] << 16);
        *mid |= ((unsigned long)myDpaResponse->Extension.Response.Data[3] << 24);
        *userData = (unsigned short)myDpaResponse->Extension.Response.Data[4];
        *userData |= ((unsigned short)myDpaResponse->Extension.Response.Data[5] << 8);
    }
    return(dpaStep);       
}

//----------------------
// Enable remote bonding
//----------------------
unsigned char enableRemoteBonding(unsigned short addr, TPerCoordinatorNodeEnableRemoteBonding_Request *remoteBonding)
{
    // Enable remote bonding
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    if(addr == DPA_ADDRESS_COORD)
    {
        // Coordinator
        myDpaRequest.PNum = PNUM_COORDINATOR;
        myDpaRequest.PCmd = CMD_COORDINATOR_ENABLE_REMOTE_BONDING;
    }
    else
    {
        // Node
        myDpaRequest.PNum = PNUM_NODE;
        myDpaRequest.PCmd = CMD_NODE_ENABLE_REMOTE_BONDING;
    }
    myDpaRequest.Extension.Plain.Data[0] = remoteBonding->BondingMask;
    myDpaRequest.Extension.Plain.Data[1] = remoteBonding->Control;
    myDpaRequest.Extension.Plain.Data[2] = remoteBonding->UserData & 0xff;
    myDpaRequest.Extension.Plain.Data[3] = remoteBonding->UserData >> 0x08;
    MyDpaLibRequest(4);
    return(dpaStep);
}

//-----
// LEDG
//-----
unsigned char ledG(unsigned short addr, unsigned char cmd)
{
    // LEDG peripheral
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_LEDG;
    myDpaRequest.PCmd = cmd;
    MyDpaLibRequest(0);
    return(dpaStep);
}

//-----
// LEDR
//-----
unsigned char ledR(unsigned short addr, unsigned char cmd)
{
    // LEDR peripheral
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_LEDR;
    myDpaRequest.PCmd = cmd;
    MyDpaLibRequest(0);
    return(dpaStep);
}

//---------
// FRC Send
//---------
unsigned char frcSend(unsigned char len)
{
    // Send FRC
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_FRC;
    myDpaRequest.PCmd = CMD_FRC_SEND;
    // Timeout for IQRF = Bonded Nodes x 130 + _RESPONSE_FRC_TIME_xxx_MS + 250 [ms]
    // + overhead for the = 2000 [ms]
    dpaTimeoutMS = (unsigned short)(networkInfo.bondedNodesCount) * 130 + 40 + 250 + 2000;
    MyDpaLibRequest(len);   
    if(dpaStep == DPA_OK)
        memcpy((void*)&prebonding.frcData[FRC_DATA_OFFSET], (void*)&myDpaResponse->Extension.Response.Data[1], FRC_DATA_SIZE);
    return(dpaStep);
}

//-----------------
// FRC extra result
//-----------------
unsigned char frcExtraResult(void)
{
    // FRC Extra result
    myDpaRequest.NAdr = DPA_ADDRESS_COORD;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_FRC;
    myDpaRequest.PCmd = CMD_FRC_EXTRARESULT;
    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
        memcpy((void*)&prebonding.frcData[FRC_EXTRA_DATA_OFFSET], (void*)myDpaResponse->Extension.Response.Data, FRC_EXTRA_DATA_SIZE);
    return(dpaStep);       
}

//---------------
// Authorize bond
//---------------
unsigned char authorizeBond(unsigned char reqAddr, unsigned short mid)
{
    for(unsigned char authorizeRetry = ANparams.authorizeRetries; authorizeRetry != 0; authorizeRetry--)
    {
        // Authorize node
        myDpaRequest.NAdr = DPA_ADDRESS_COORD;
        myDpaRequest.HwProfile = HWPID_DoNotCheck;
        myDpaRequest.PNum = PNUM_COORDINATOR;
        myDpaRequest.PCmd = CMD_COORDINATOR_AUTHORIZE_BOND;
        myDpaRequest.Extension.Plain.Data[0] = reqAddr;
        myDpaRequest.Extension.Plain.Data[1] = mid & 0xff;
        myDpaRequest.Extension.Plain.Data[2] = mid >> 0x08;
        MyDpaLibRequest(3);

        delayMS((unsigned long)(networkInfo.bondedNodesCount) * 50 + 150);

        if(authorizeRetry != 1)
        {
            prebonding.param = reqAddr;
            notifyMainApp(EVT_COOR_REMOVING_BOND, EVT_WITH_PARAM);
            removeBondedNode(reqAddr);
        }
        else
        {
            // Authorization OK ?
            if(dpaStep == DPA_OK)
            {
                // Yes
                prebonding.param = myDpaResponse->Extension.Response.Data[1];
                setBitValue(networkInfo.bondedNodesMap, reqAddr);
                return(DPA_OK);
            }
        }
    }
 
    return(DPA_ERROR);
}

//---------------
// Read node info
//---------------
unsigned char readNodeInfo(unsigned short addr)
{
    // Read node info
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_NODE;
    myDpaRequest.PCmd = CMD_NODE_READ;
    MyDpaLibRequest(0);
    return(dpaStep);
}

//------------
// Remove bond
//------------
unsigned char removeBond(unsigned short addr)
{
    // Remove bond
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_NODE;
    myDpaRequest.PCmd = CMD_NODE_REMOVE_BOND;
    MyDpaLibRequest(0);
    return(dpaStep);
}

//--------------------------------------------
// Remove bond and restart using batch command
//--------------------------------------------
unsigned char removeBondRestart(unsigned short addr)
{
    // Remove bond
    myDpaRequest.Extension.Plain.Data[0] = 5;
    myDpaRequest.Extension.Plain.Data[1] = PNUM_NODE;
    myDpaRequest.Extension.Plain.Data[2] = CMD_NODE_REMOVE_BOND;
    myDpaRequest.Extension.Plain.Data[3] = HWPID_DoNotCheck & 0xff;
    myDpaRequest.Extension.Plain.Data[4] = HWPID_DoNotCheck >> 0x08;
    // Reset
    myDpaRequest.Extension.Plain.Data[5] = 5;
    myDpaRequest.Extension.Plain.Data[6] = PNUM_OS;
    myDpaRequest.Extension.Plain.Data[7] = CMD_OS_RESTART;
    myDpaRequest.Extension.Plain.Data[8] = HWPID_DoNotCheck & 0xff;
    myDpaRequest.Extension.Plain.Data[9] = HWPID_DoNotCheck >> 0x08;
    // EndBatch
    myDpaRequest.Extension.Plain.Data[10] = 0;
    // Batch command
    return(sendBatch(addr, 11));
}

//--------------
// Check new MID
//--------------
unsigned char checkNewMID(unsigned long newMID)
{
    for(unsigned char i = 0; i < prebonding.MIDcount; i++)
    {
        // Is the newMID in prebondedMIDs list ?
        if(prebonding.MIDlist[i] == newMID)
            return(AN_ERROR);
    }
    
    // MID OK
    return(AN_OK);    
}

//-------------------------------------------
// Check the lower 16 bits of MID is the same
//-------------------------------------------
unsigned char checkDuplicitMID(unsigned char idx)
{
    for(unsigned char index = 0; index < prebonding.MIDcount; index++)
    {       
        if(index == idx)
            continue;

        if((prebonding.MIDlist[idx] & 0xffff) == (prebonding.MIDlist[index] & 0xffff))
            return(AN_ERROR);
    }
    
    // MID OK
    return(AN_OK);     
}

//-----------------------------------------
// Remove all bond at nodes and coordinator
//-----------------------------------------
void removeAllBonds(void)
{
    // Send broadcast batch command to remove bonds and reset nodes
    notifyMainApp(EVT_REMOVE_ALL_BONDS, EVT_WITHOUT_PARAM);
    removeBondRestart(DPA_ADDRESS_BROADCAST);
  
    // Remove bonds at coordinator too
    clearAllBond();
}

//------------------------------
// Terminate autonetwork process
//------------------------------
void terminateProcess(void)
{
    TPerCoordinatorNodeEnableRemoteBonding_Request enableBonding;

    // Disable bonding at nodes and coordinator
    enableBonding.BondingMask = 0;
    enableBonding.Control = 0;
    enableBonding.UserData = 0;
    notifyMainApp(EVT_NODE_DISABLE_PREBONDING, EVT_WITHOUT_PARAM);
    enableRemoteBonding(DPA_ADDRESS_BROADCAST, &enableBonding);
    notifyMainApp(EVT_COOR_DISABLE_PREBONDING, EVT_WITHOUT_PARAM);
    enableRemoteBonding(DPA_ADDRESS_COORD, &enableBonding);
    notifyMainApp(EVT_AN_PROCESS_STOPPED, EVT_WITHOUT_PARAM);
    ledR(DPA_ADDRESS_BROADCAST, CMD_LED_SET_OFF);
}

//---------------------------
// Autonetwork initialization
//---------------------------
void autonetworkInit(autonetworkNotify_CB notify_CB, autonetworkCheckInput_CB checkInput_CB)
{
    // DPA init
    DPA_Init();
    DPA_SetAnswerHandler(MyDpaAnswerHandler);
    dpaStep = NO_WAITING;
    dpaTimeoutMS = 0;
    
    // Set callback functions
    if(notify_CB != NULL)
        notify_cb = notify_CB;   
    if(checkInput_CB != NULL)
        checkInput_cb = checkInput_CB;
}

//---------------------
// Autonetwork function
//---------------------
unsigned char autonetwork(T_AN_PARAMS *par)
{  
    unsigned long delay_ms;
    
    // initialization
    terminate = 0x00;
    memcpy((void*)&ANparams, (void*)par, sizeof(T_AN_PARAMS));
    autonetworkState.newtorkInfo = &networkInfo;
    autonetworkState.prebondingInfo = &prebonding;
    autonetworkState.params = &ANparams;

    // Notify main application
    notifyMainApp(EVT_AN_PROCESS_STARTED, EVT_WITHOUT_PARAM);

    // Get addresing info
    notifyMainApp(EVT_GET_NETWORK_INFO, EVT_WITHOUT_PARAM);
    getAddrInfo();
    prebonding.origNodesCount = networkInfo.bondedNodesCount;

    // Get bonded nodes bitmap
    getBondedNodes();

    // Get discovered nodes bitmap
    getDiscoveredNodes();

    // Set DPA hops
    setHops(0xff, 0xff);

    // Set DPA prarameter
    setDPAParam(0x00);
    prebonding.nextAddr = DPA_ADDRESS_MAX;    
    for( prebonding.round = 1; networkInfo.bondedNodesCount != DPA_ADDRESS_MAX; prebonding.round++)
    {
        // Initialize variables
        prebonding.MIDcount = 0;
        prebonding.newNode = 0x00;
        // Reset bitmap of nodes provided prebonding, new adrress and MID
        memset(prebonding.newNodesMap, 0, NODE_BITMAP_SIZE);
        memset((unsigned char*)prebonding.MIDlist, 0, MID_BUFFER_SIZE);

        // Set bonding mask
        prebonding.bondingMask = 0;                // Default value for 0 nodes
        if(networkInfo.bondedNodesCount > 0)
        {
            unsigned char x = networkInfo.bondedNodesCount;
            prebonding.bondingMask = 0xff;         // Default value for > 127 nodes
            while((x & 0x80) == 0)
            {
                x <<= 1;
                prebonding.bondingMask >>= 1;
            }
        }

        // 100ms unit
        prebonding.wTimeout = (unsigned short)(((long)ANparams.temporaryAddressTimeout * 1000) / 100);
        // Check prebondingInterval, minimal interval is 15 sec.        
        if(ANparams.prebondingInterval < AN_MIN_PREBONDING_TIME)
            ANparams.prebondingInterval = AN_MIN_PREBONDING_TIME;
        prebonding.waitBonding = ANparams.prebondingInterval;        
        prebonding.waitBonding10ms = prebonding.waitBonding * 100;

        // Notify main application
        notifyMainApp(EVT_ROUND_START, EVT_WITH_PARAM);

        // Send batch command to all nodes, if any
        if(networkInfo.bondedNodesCount > 0)
        {
            // Notify main application
            notifyMainApp(EVT_NODE_ENABLE_PREBONDING, EVT_WITH_PARAM);

            // LEDR = 1
            myDpaRequest.Extension.Plain.Data[0] = 5;
            myDpaRequest.Extension.Plain.Data[1] = PNUM_LEDR;
            myDpaRequest.Extension.Plain.Data[2] = CMD_LED_SET_ON;
            myDpaRequest.Extension.Plain.Data[3] = HWPID_DoNotCheck & 0xff;
            myDpaRequest.Extension.Plain.Data[4] = HWPID_DoNotCheck >> 0x08;
            // Enable prebonding
            myDpaRequest.Extension.Plain.Data[5] = 9;
            myDpaRequest.Extension.Plain.Data[6] = PNUM_NODE;
            myDpaRequest.Extension.Plain.Data[7] = CMD_NODE_ENABLE_REMOTE_BONDING;
            myDpaRequest.Extension.Plain.Data[8] = HWPID_DoNotCheck & 0xff;
            myDpaRequest.Extension.Plain.Data[9] = HWPID_DoNotCheck >> 0x08;
            myDpaRequest.Extension.Plain.Data[10] = prebonding.bondingMask;
            myDpaRequest.Extension.Plain.Data[11] = 1;
            myDpaRequest.Extension.Plain.Data[12] = prebonding.wTimeout & 0xff;
            myDpaRequest.Extension.Plain.Data[13] = prebonding.wTimeout >> 8;
            myDpaRequest.Extension.Plain.Data[14] = 9;
            // Send P2P packet to allow prebonding
            myDpaRequest.Extension.Plain.Data[15] = PNUM_USER,
            myDpaRequest.Extension.Plain.Data[16] = 0;
            myDpaRequest.Extension.Plain.Data[17] = HWPID_DoNotCheck & 0xff;
            myDpaRequest.Extension.Plain.Data[18] = HWPID_DoNotCheck >> 0x08;
            myDpaRequest.Extension.Plain.Data[19] = 0x55;
            myDpaRequest.Extension.Plain.Data[20] = prebonding.waitBonding10ms & 0xff;
            myDpaRequest.Extension.Plain.Data[21] = prebonding.waitBonding10ms >> 8 ;
            myDpaRequest.Extension.Plain.Data[22] = networkInfo.bondedNodesCount + 3;
            // EndBatch
            myDpaRequest.Extension.Plain.Data[23] = 0;
            // Broadcast batch command
            sendBatch(DPA_ADDRESS_BROADCAST, 24);
            delayMS(((unsigned long)(networkInfo.bondedNodesCount) + 1) * 40 + (unsigned long)(networkInfo.bondedNodesCount) * 60);
            // Terminate process ?
            if(terminate == ERR_PROCESS_TERMINATED)
            {
                terminateProcess();
                return(ERR_PROCESS_TERMINATED);
            }
        }

        // Send batch command to coordinator
        // Enable prebonding
        myDpaRequest.Extension.Plain.Data[0] = 9;
        myDpaRequest.Extension.Plain.Data[1] = PNUM_COORDINATOR;
        myDpaRequest.Extension.Plain.Data[2] = CMD_COORDINATOR_ENABLE_REMOTE_BONDING;
        myDpaRequest.Extension.Plain.Data[3] = HWPID_DoNotCheck & 0xff;
        myDpaRequest.Extension.Plain.Data[4] = HWPID_DoNotCheck >> 0x08;
        myDpaRequest.Extension.Plain.Data[5] = prebonding.bondingMask;
        myDpaRequest.Extension.Plain.Data[6] = 1;
        myDpaRequest.Extension.Plain.Data[7] = prebonding.wTimeout & 0xff;
        myDpaRequest.Extension.Plain.Data[8] = prebonding.wTimeout >> 8;
        // Send P2P packet to allow prebonding
        myDpaRequest.Extension.Plain.Data[9] = 9;
        myDpaRequest.Extension.Plain.Data[10] = PNUM_USER;
        myDpaRequest.Extension.Plain.Data[11] = 0;
        myDpaRequest.Extension.Plain.Data[12] = HWPID_DoNotCheck & 0xff;
        myDpaRequest.Extension.Plain.Data[13] = HWPID_DoNotCheck >> 0x08;
        myDpaRequest.Extension.Plain.Data[14] = 0x55;
        myDpaRequest.Extension.Plain.Data[15] = prebonding.waitBonding10ms & 0xff;
        myDpaRequest.Extension.Plain.Data[16] = prebonding.waitBonding10ms >> 8;
        myDpaRequest.Extension.Plain.Data[17] = 1;
        // EndBatch
        myDpaRequest.Extension.Plain.Data[18] = 0;

        // Notify main application
        notifyMainApp(EVT_COOR_ENABLE_PREBONDING, EVT_WITH_PARAM);

        // Send the batch command to [C]
        sendBatch(DPA_ADDRESS_COORD, 19);

        // Waiting for prebonding
        prebonding.delay = prebonding.waitBonding;
        notifyMainApp(EVT_WAIT_PREBONDING, EVT_WITH_PARAM);        
        delay_ms = (unsigned long)(prebonding.waitBonding) * 1000 + 1000;        
        delayMS(delay_ms);
        // Terminate process ?
        if(terminate == ERR_PROCESS_TERMINATED)
        {
            terminateProcess();
            return(ERR_PROCESS_TERMINATED);
        }

        // Disable remote bonding at coordinator
        TPerCoordinatorNodeEnableRemoteBonding_Request enableBonding;
        enableBonding.BondingMask = 0;
        enableBonding.Control = 0;
        enableBonding.UserData = 0;
        notifyMainApp(EVT_COOR_DISABLE_PREBONDING, EVT_WITHOUT_PARAM);
        enableRemoteBonding(DPA_ADDRESS_COORD, &enableBonding);

        // Try to read prebonded MID from coordinator
        if(readPrebondedMID(DPA_ADDRESS_COORD, &prebonding.MID, &prebonding.userData) == DPA_OK)
        {
            // Check the new MID
            if(checkNewMID(prebonding.MID) == 0)
            {
                // Notify main application
                notifyMainApp(EVT_COOR_READ_MID, EVT_WITH_PARAM);                
                prebonding.MIDlist[prebonding.MIDcount++] = prebonding.MID;

                // Terminate process ?
                if(terminate == ERR_PROCESS_TERMINATED)
                {
                    terminateProcess();
                    return(ERR_PROCESS_TERMINATED);
                }
            }
        }

        // Any bonded nodes ?
        if(networkInfo.bondedNodesCount)
        {
            // Send FRC command Prebonding to get bitmap of nodes provided prebonding
            notifyMainApp(EVT_FRC_DISABLE_PREBONDING, EVT_WITHOUT_PARAM);
            myDpaRequest.Extension.Plain.Data[0] = 0;       // Command 0 - Prebonding
            myDpaRequest.Extension.Plain.Data[1] = 0x01;
            myDpaRequest.Extension.Plain.Data[2] = 0x00;
            frcSend(3);
            frcExtraResult();            

            // Get bitmap of nodes provided prebonding, read MID of prebonded nodes
            for(unsigned char addr = 1; addr <= DPA_ADDRESS_MAX; addr++)
            {
                // Terminate process ?
                if(terminate == ERR_PROCESS_TERMINATED)
                {
                    terminateProcess();
                    return(ERR_PROCESS_TERMINATED);
                }

                // Check the node is bonded
                prebonding.param = addr;
                unsigned char nodeBonded = isNodeBonded(addr);
                unsigned char bit0OK = 0x00;

                // Bit0 is set (node sent response to FRC) ?
                if(getBitValue(prebonding.frcData, addr) == 0x01)
                {
                    if(nodeBonded)
                        bit0OK = 0xff;
                    else
                        notifyMainApp(EVT_FRC_DISABLE_PREBONDING_BIT0_ERR, EVT_WITH_PARAM);
                }
                
                // Is Bit1 set (node provided the prebonding) ?
                if(getBitValue(prebonding.frcData + 32, addr) == 0x01)
                {
                    // Is node providing the prebonding already bonded ?
                    if(nodeBonded)
                    {
                        // Node sent response to FRC ?
                        if(bit0OK)
                        {
                            // Read the MID of prebonded node
                            if(readPrebondedMID(addr, &prebonding.MID, &prebonding.userData) == DPA_OK)
                            {
                                // Check the current MID
                                if(checkNewMID(prebonding.MID) == 0x00)
                                {
                                    notifyMainApp(EVT_NODE_READ_MID, EVT_WITH_PARAM);
                                    prebonding.MIDlist[prebonding.MIDcount++] = prebonding.MID;          
                                    if(prebonding.MIDcount >= (MID_BUFFER_SIZE - 1))
                                    {
                                        // Error, maximum prebonded nodes reached
                                        notifyMainApp(EVT_MAX_NODES_PREBONDED, EVT_WITHOUT_PARAM);
                                        terminateProcess();
                                        return(ERR_MAX_NODES_PREBONDED);
                                    }
                                }
                            }
                        }
                    }
                    else
                        notifyMainApp(EVT_FRC_DISABLE_PREBONDING_BIT1_ERR, EVT_WITH_PARAM);
                }
            }
        }
        
        // Authorize prebonded nodes
        for(unsigned char addr = 0; addr < prebonding.MIDcount; addr++)
        {
            prebonding.MID = prebonding.MIDlist[addr];
            if(checkDuplicitMID(addr) == 0)
            {
                // OK, Get next free address
                prebonding.nextAddr = nextFreeAddr(prebonding.nextAddr);
                if(prebonding.nextAddr == 0xff)
                {
                    // Error, no free address, terminate process
                    notifyMainApp(EVT_NO_FREE_ADDRESS, EVT_WITHOUT_PARAM);
                    terminateProcess();
                    return(ERR_NO_FREE_ADDRESS);
                }            

                // Authorize node
                notifyMainApp(EVT_AUTHORIZE_BOND, EVT_WITH_PARAM);
                if(authorizeBond(prebonding.nextAddr, (unsigned short)prebonding.MIDlist[addr]) == DPA_OK)
                {
                    setBitValue(prebonding.newNodesMap, prebonding.nextAddr);
                    prebonding.newNode = 0xff;
                    notifyMainApp(EVT_AUTHORIZE_BOND_OK, EVT_WITH_PARAM);
                }
            }
            delayMS(10);
        }
        
        // Any new node ?
        if(prebonding.newNode)
        {
            // Send FRC command Prebonding to check the new nodes
            notifyMainApp(EVT_FRC_CHECK_NEW_NODES, EVT_WITHOUT_PARAM);
            myDpaRequest.Extension.Plain.Data[0] = 0;       // Command 0 - Prebonding
            myDpaRequest.Extension.Plain.Data[1] = 0x01;
            myDpaRequest.Extension.Plain.Data[2] = 0x00;
            frcSend(3);
            
            // Check new nodes
            for(unsigned char addr = 1; addr <= DPA_ADDRESS_MAX; addr++)
            {
                if(getBitValue(prebonding.newNodesMap, addr))
                {
                    // Bit0 is cleared (new node isn't responding to FRC) ?
                    if(getBitValue(prebonding.frcData, addr) == 0x00)
                    {
                        // No response from the node                                                                    
                        prebonding.param = addr;
                        // Remove and reset the node   
                        notifyMainApp(EVT_NODE_REMOTE_UNBOND, EVT_WITH_PARAM);
                        if(removeBondRestart(addr) != DPA_OK)
                        {
                             delay_ms = (unsigned long)(networkInfo.bondedNodesCount + 1) * 80;
                             delayMS(delay_ms);
                        }
                        // Remove node at coordinator too
                        notifyMainApp(EVT_COOR_REMOVING_BOND, EVT_WITH_PARAM);
                        removeBondedNode(addr);
                    }
                }
            }
            
            // Remove and reset node with temporary address 0xfe
            removeBond(DPA_ADDRESS_TEMP);

            // Run Discovery
            for(unsigned char discoveryRetry = ANparams.discoveryRetries; discoveryRetry != 0; discoveryRetry--)
            {
                notifyMainApp(EVT_DISCOVERY, EVT_WITHOUT_PARAM);
                if(discovery(ANparams.discoveryTxPower, 0) == DPA_OK)
                    notifyMainApp(EVT_DISCOVERY_OK, EVT_WITH_PARAM);
                notifyMainApp(EVT_DISCOVERY_WAIT, EVT_WITHOUT_PARAM);
                for( ; ; )
                {
                    // Wait for finish the Discovery
                     delayMS(1000);
                    // Terminate process ?
                    if(terminate == ERR_PROCESS_TERMINATED)
                    {
                        terminateProcess();
                        return(ERR_PROCESS_TERMINATED);
                    }
                    // Get addresing info
                    if(getAddrInfo() == DPA_OK)
                        break;
                }

                // Get bonded nodes bitmap
                getBondedNodes();

                // Get discovered nodes bitmap
                getDiscoveredNodes();
                networkInfo.discoveredNodesCount = 0;
                for(unsigned addr = 1; addr <= DPA_ADDRESS_MAX; addr++)
                    if(getBitValue(networkInfo.discoveredNodesMap, addr) == 1)
                        networkInfo.discoveredNodesCount++;
                // Terminate process ?
                if(terminate == ERR_PROCESS_TERMINATED)
                {
                    terminateProcess();
                    return(ERR_PROCESS_TERMINATED);
                }
                if(networkInfo.discoveredNodesCount == networkInfo.bondedNodesCount)
                    break;
            }
        }
        else
            notifyMainApp(EVT_NO_NEW_NODE_PREBONDED, EVT_WITHOUT_PARAM);
    }

    terminateProcess();
    return(AN_OK);
}
