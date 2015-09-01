#include <string.h>
#include "user.h"
#include "uart.h"
#include "dpa_library.h"
#include "autonetwork.h"

T_PREBONDING_INFO prebonding;
T_AN_STATE autonetworkState;

T_AN_PARAMS ANparams;
T_DPA_PACKET myDpaRequest;
T_DPA_PACKET *myDpaResponse;
T_NETWORK_INFO networkInfo;
unsigned char dpaStep;
volatile unsigned short dpaTimeoutMS = 0;
volatile unsigned long delay = 0;
volatile unsigned char uartTxCompleted, terminate = 0;

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
unsigned char setBitValue(unsigned char *bitField, unsigned char bitAddr)
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
            return(0xff);
    }
}

//---------------------------
//DPA UART send byte function
//---------------------------
void DPA_SendUartByte(unsigned char Tx_Byte)
{
    uartTxCompleted = 0;
    uartPutChar(Tx_Byte);
    while(!uartTxCompleted);
}

//-------------------------
//UART TX interrupt handler
//-------------------------
void MyDpaUartTxHandler(void)
{
    uartTxCompleted = 1;
}

//-------------------------
//UART RX interrupt handler
//-------------------------
void MyDpaUartRxHandler(void)
{
    unsigned char Rx_Byte;

    Rx_Byte = uartGetChar();
    if((dpaStep == CONFIRMATION_WAITING) || (dpaStep == RESPONSE_WAITING))
        DPA_ReceiveUartByte(Rx_Byte);
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
    if(delay)
        delay--;
}

//---------------
// Delay function
//---------------
void delayMS(unsigned long ms)
{
    if(ms != 0)
    {
        delay = ms;
        while(delay)
        {
            // Terminate command ?
            if(checkInput(NULL) == CMD_STOP_AN)
            {
                terminate = ERR_PROCESS_TERMINATED;
                return;
            }
        }
    }
}

//------------------------
// Notify main application
//------------------------
void notifyMainApp(unsigned char eventCode, unsigned char param)
{
    if(param == 0)
        autonetworkHandler(eventCode, NULL);
    else
        autonetworkHandler(eventCode, &autonetworkState);
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
        if(myDpaRequest.NAdr != 0x00ff)
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
        if(dpaAnswerPkt->NAdr != 0x0000)
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
    if (myDpaRequest.NAdr == 0x0000)
        dpaStep = RESPONSE_WAITING;         // DPA answer would be response
    else
        dpaStep = CONFIRMATION_WAITING;     // DPA answer would be confirmation

    if(dpaTimeoutMS == 0)
        dpaTimeoutMS = 500;                 // Default timeout is 500 ms

    // Send the DPA request, wait for result
    DPA_SendRequest(&myDpaRequest, len);
    while(dpaStep < DPA_OK)
    {
        DPA_LibraryDriver();                 // DPA library handler
    }
    if(dpaStep & DPA_TIMEOUT)
    {
        if((dpaStep & 0x7f) == CONFIRMATION_WAITING)
            notifyMainApp(EVT_DPA_CONFIRMATION_TIMEOUT, 0);
        else
            notifyMainApp(EVT_DPA_RESPONSE_TIMEOUT, 0);
    }
}

//---------------------------
// Get addressing information
//---------------------------
unsigned char getAddrInfo(void)
{
    // Get addresing info
    myDpaRequest.NAdr = 0x0000;
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
    myDpaRequest.NAdr = 0x0000;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_DISCOVERED_DEVICES;
    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
        memcpy(networkInfo.discoveredNodesMap, (void*)myDpaResponse->Extension.Response.Data, 32);
    return(dpaStep);
}

//-----------------
// Get bonded nodes
//-----------------
unsigned char getBondedNodes(void)
{
    // Get bonded nodes
    myDpaRequest.NAdr = 0x0000;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_COORDINATOR;
    myDpaRequest.PCmd = CMD_COORDINATOR_BONDED_DEVICES;
    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
        memcpy(networkInfo.bondedNodesMap, (void*)myDpaResponse->Extension.Response.Data, 32);
    return(dpaStep);}

//----------------
// Clear all bonds
//----------------
unsigned char clearAllBond(void)
{
    // Clear all bonds
    myDpaRequest.NAdr = 0x0000;
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
    myDpaRequest.NAdr = 0x0000;
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
    myDpaRequest.NAdr = 0x0000;
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
    myDpaRequest.NAdr = 0x0000;
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
    myDpaRequest.NAdr = 0x0000;
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
    if(addr == 0)
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
    if(addr == 0)
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
    myDpaRequest.NAdr = 0x0000;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_FRC;
    myDpaRequest.PCmd = CMD_FRC_SEND;
    dpaTimeoutMS = (unsigned short)(networkInfo.bondedNodesCount) * 20 + (unsigned short)(networkInfo.discoveredNodesCount) * 90 + 500;
    MyDpaLibRequest(len);   
    if(dpaStep == DPA_OK)
        memcpy(prebonding.frcData, (void*)&myDpaResponse->Extension.Response.Data[1], 55);
    return(dpaStep);
}

//-----------------
// FRC extra result
//-----------------
unsigned char frcExtraResult(void)
{
    // FRC Extra result
    myDpaRequest.NAdr = 0x0000;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_FRC;
    myDpaRequest.PCmd = CMD_FRC_EXTRARESULT;
    MyDpaLibRequest(0);
    if(dpaStep == DPA_OK)
        memcpy((void*)&prebonding.frcData[55], (void*)myDpaResponse->Extension.Response.Data, 9);
    return(dpaStep);       
}

//---------------
// Authorize bond
//---------------
unsigned char authorizeBond(unsigned char reqAddr, unsigned short mid)
{
    unsigned char authorizeRetry;
   
    for(authorizeRetry = ANparams.authorizeRetries; authorizeRetry != 0; authorizeRetry--)
    {
        // Authorize node
        myDpaRequest.NAdr = 0x0000;
        myDpaRequest.HwProfile = HWPID_DoNotCheck;
        myDpaRequest.PNum = PNUM_COORDINATOR;
        myDpaRequest.PCmd = 0x0d;//CMD_COORDINATOR_AUTHORIZE_BOND;
        myDpaRequest.Extension.Plain.Data[0] = reqAddr;
        myDpaRequest.Extension.Plain.Data[1] = mid & 0xff;
        myDpaRequest.Extension.Plain.Data[2] = mid >> 0x08;
        MyDpaLibRequest(3);
        if(dpaStep != DPA_OK)
            return(DPA_ERROR);
        delayMS((unsigned long)(networkInfo.bondedNodesCount) * 40 + 150);
        if(authorizeRetry != 1)
        {
            prebonding.param = reqAddr;
            notifyMainApp(EVT_COOR_REMOVING_BOND, 1);
            removeBondedNode(reqAddr);
        }
        else
        {
            prebonding.param = myDpaResponse->Extension.Response.Data[1];
            setBitValue(networkInfo.bondedNodesMap, reqAddr);
            return(DPA_OK);
        }
    }
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

//------------------------------------------
// Remove bond and reset using batch command
//------------------------------------------
unsigned char removeBondReset(unsigned short addr)
{
    // Remove bond
    myDpaRequest.Extension.Plain.Data[0] = 5;
    myDpaRequest.Extension.Plain.Data[1] = PNUM_NODE;
    myDpaRequest.Extension.Plain.Data[2] = CMD_NODE_REMOVE_BOND;
    myDpaRequest.Extension.Plain.Data[3] = 0xff;
    myDpaRequest.Extension.Plain.Data[4] = 0xff;
    // Reset
    myDpaRequest.Extension.Plain.Data[5] = 5;
    myDpaRequest.Extension.Plain.Data[6] = PNUM_OS;
    myDpaRequest.Extension.Plain.Data[7] = CMD_OS_RESET;
    myDpaRequest.Extension.Plain.Data[8] = 0xff;
    myDpaRequest.Extension.Plain.Data[9] = 0xff;
    // EndBatch
    myDpaRequest.Extension.Plain.Data[10] = 0;
    // Batch command
    return(sendBatch(addr, 11));
}

//----------------
// Reset TR module
//----------------
unsigned char resetModule(unsigned short addr)
{
    // Reset TR module
    myDpaRequest.NAdr = addr;
    myDpaRequest.HwProfile = HWPID_DoNotCheck;
    myDpaRequest.PNum = PNUM_OS;
    myDpaRequest.PCmd = CMD_OS_RESET;
    MyDpaLibRequest(0);
    return(dpaStep);
}

//--------------
// Check new MID
//--------------
unsigned char checkNewMID(unsigned long newMID)
{
    unsigned char i;
    
    for(i = 0; i < prebonding.MIDcount; i++)
    {
        // Is the newMID in prebondedMIDs list ?
        if(prebonding.MIDlist[i] == newMID)
            return(0xff);
    }
    
    // MID OK
    return(0x00);    
}

//-------------------------------------------
// Check the lower 16 bits of MID is the same
//-------------------------------------------
unsigned char checkDuplicitMID(unsigned char idx)
{
    unsigned char index;
        
    for(index = 0; index < prebonding.MIDcount; index++)
    {       
        if(index == idx)
            continue;

        if((prebonding.MIDlist[idx] & 0xffff) == (prebonding.MIDlist[index] & 0xffff))
            return(0xff);
    }
    
    // MID OK
    return(0x00);     
}

//-----------------------------------------
// Remove all bond at nodes and coordinator
//-----------------------------------------
void removeAllBonds(void)
{
    // Send broadcast batch command to remove bonds and reset nodes
    notifyMainApp(EVT_REMOVE_ALL_BONDS, 0);
    removeBondReset(0xff);
    
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
    notifyMainApp(EVT_NODE_DISABLE_PREBONDING, 0);
    enableRemoteBonding(0x00ff, &enableBonding);
    notifyMainApp(EVT_COOR_DISABLE_PREBONDING, 0);
    enableRemoteBonding(0x0000, &enableBonding);
    notifyMainApp(EVT_AN_PROCESS_STOPPED, 0);
    ledR(0x00ff, CMD_LED_SET_OFF);
}

//---------------------------
// DPA library initialization
//---------------------------
void autonetworkInit(void)
{
    DPA_Init();
    DPA_SetAnswerHandler(MyDpaAnswerHandler);
    dpaStep = NO_WAITING;
    dpaTimeoutMS = 0;
}

//---------------------
// Autonetwork function
//---------------------
unsigned char autonetwork(T_AN_PARAMS *par)
{  
    unsigned char discoveryRetry;
    unsigned char addr, bit0OK, nodeBonded;
    TPerCoordinatorNodeEnableRemoteBonding_Request enableBonding;
    unsigned long delay;

    // initialization
    terminate = 0x00;
    memcpy((void*)&ANparams, (void*)par, sizeof(T_AN_PARAMS));
    autonetworkState.newtorkInfo = &networkInfo;
    autonetworkState.prebondingInfo = &prebonding;
    autonetworkState.params = &ANparams;

    // Notify main application
    notifyMainApp(EVT_AN_PROCESS_STARTED, 0);

    // Get addresing info
    notifyMainApp(EVT_GET_NETWORK_INFO, 0);
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
        memset(prebonding.newNodesMap, 0, 32);
        memset((unsigned char*)prebonding.MIDlist, 0, 512);

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
        prebonding.waitBonding = networkInfo.bondedNodesCount;
        if(prebonding.waitBonding < 10)
            prebonding.waitBonding = 10;
        if(prebonding.waitBonding > ANparams.prebondingInterval)
            prebonding.waitBonding = ANparams.prebondingInterval;
        prebonding.waitBonding10ms = prebonding.waitBonding * 100;

        // Notify main application
        notifyMainApp(EVT_ROUND_START, 1);

        // Send batch command to all nodes, if any
        if(networkInfo.bondedNodesCount > 0)
        {
            // Notify main application
            notifyMainApp(EVT_NODE_ENABLE_PREBONDING, 1);

            // LEDR = 1
            myDpaRequest.Extension.Plain.Data[0] = 5;
            myDpaRequest.Extension.Plain.Data[1] = PNUM_LEDR;
            myDpaRequest.Extension.Plain.Data[2] = CMD_LED_SET_ON;
            myDpaRequest.Extension.Plain.Data[3] = 0xff;
            myDpaRequest.Extension.Plain.Data[4] = 0xff;
            // Enable prebonding
            myDpaRequest.Extension.Plain.Data[5] = 9;
            myDpaRequest.Extension.Plain.Data[6] = PNUM_NODE;
            myDpaRequest.Extension.Plain.Data[7] = CMD_NODE_ENABLE_REMOTE_BONDING;
            myDpaRequest.Extension.Plain.Data[8] = 0xff;
            myDpaRequest.Extension.Plain.Data[9] = 0xff;
            myDpaRequest.Extension.Plain.Data[10] = prebonding.bondingMask;
            myDpaRequest.Extension.Plain.Data[11] = 1;
            myDpaRequest.Extension.Plain.Data[12] = prebonding.wTimeout & 0xff;
            myDpaRequest.Extension.Plain.Data[13] = prebonding.wTimeout >> 8;
            myDpaRequest.Extension.Plain.Data[14] = 9;
            // Send P2P packet to allow prebonding
            myDpaRequest.Extension.Plain.Data[15] = PNUM_USER,
            myDpaRequest.Extension.Plain.Data[16] = 0;
            myDpaRequest.Extension.Plain.Data[17] = 0xff;
            myDpaRequest.Extension.Plain.Data[18] = 0xff;
            myDpaRequest.Extension.Plain.Data[19] = 0x55;
            myDpaRequest.Extension.Plain.Data[20] = prebonding.waitBonding10ms & 0xff;
            myDpaRequest.Extension.Plain.Data[21] = prebonding.waitBonding10ms >> 8 ;
            myDpaRequest.Extension.Plain.Data[22] = networkInfo.bondedNodesCount + 3;
            // EndBatch
            myDpaRequest.Extension.Plain.Data[23] = 0;
            // Broadcast batch command
            sendBatch(0xff, 24);
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
        myDpaRequest.Extension.Plain.Data[3] = 0xff;
        myDpaRequest.Extension.Plain.Data[4] = 0xff;
        myDpaRequest.Extension.Plain.Data[5] = prebonding.bondingMask;
        myDpaRequest.Extension.Plain.Data[6] = 1;
        myDpaRequest.Extension.Plain.Data[7] = prebonding.wTimeout & 0xff;
        myDpaRequest.Extension.Plain.Data[8] = prebonding.wTimeout >> 8;
        // Send P2P packet to allow prebonding
        myDpaRequest.Extension.Plain.Data[9] = 9;
        myDpaRequest.Extension.Plain.Data[10] = PNUM_USER;
        myDpaRequest.Extension.Plain.Data[11] = 0;
        myDpaRequest.Extension.Plain.Data[12] = 0xff;
        myDpaRequest.Extension.Plain.Data[13] = 0xff;
        myDpaRequest.Extension.Plain.Data[14] = 0x55;
        myDpaRequest.Extension.Plain.Data[15] = prebonding.waitBonding10ms & 0xff;
        myDpaRequest.Extension.Plain.Data[16] = prebonding.waitBonding10ms >> 8;
        myDpaRequest.Extension.Plain.Data[17] = 1;
        // EndBatch
        myDpaRequest.Extension.Plain.Data[18] = 0;

        // Notify main application
        notifyMainApp(EVT_COOR_ENABLE_PREBONDING, 1);

        // Send the batch command to [C]
        sendBatch(0x00, 19);

        // Waiting for prebonding
        delay = (unsigned long)(prebonding.waitBonding) * 1000 - (unsigned long)(networkInfo.bondedNodesCount + 1) * 40 + (unsigned long)(networkInfo.bondedNodesCount) * 60 + 1000;        
        prebonding.delay = delay / 1000;
        notifyMainApp(EVT_WAIT_PREBONDING, 1);
        delayMS(delay);
        // Terminate process ?
        if(terminate == ERR_PROCESS_TERMINATED)
        {
            terminateProcess();
            return(ERR_PROCESS_TERMINATED);
        }

        // Disable remote bonding at coordinator
        enableBonding.BondingMask = 0;
        enableBonding.Control = 0;
        enableBonding.UserData = 0;
        notifyMainApp(EVT_COOR_DISABLE_PREBONDING, 0);
        enableRemoteBonding(0x00, &enableBonding);

        // Try to read prebonded MID from coordinator
        if(readPrebondedMID(0x00, &prebonding.MID, &prebonding.userData) == DPA_OK)
        {
            // Check the new MID
            if(checkNewMID(prebonding.MID) == 0)
            {
                // Notify main application
                notifyMainApp(EVT_COOR_READ_MID, 1);                
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
            notifyMainApp(EVT_FRC_DISABLE_PREBONDING, 0);
            myDpaRequest.Extension.Plain.Data[0] = 0;       // Command 0 - Prebonding
            myDpaRequest.Extension.Plain.Data[1] = 0x01;
            myDpaRequest.Extension.Plain.Data[2] = 0x00;
            frcSend(3);
            frcExtraResult();            

            // Get bitmap of nodes provided prebonding, read MID of prebonded nodes
            for(addr = 1; addr <= DPA_ADDRESS_MAX; addr++)
            {
                // Terminate process ?
                if(terminate == ERR_PROCESS_TERMINATED)
                {
                    terminateProcess();
                    return(ERR_PROCESS_TERMINATED);
                }

                // Check the node is bonded
                prebonding.param = addr;
                nodeBonded = isNodeBonded(addr);
                bit0OK = 0x00;

                // Bit0 is set (node sent response to FRC) ?
                if(getBitValue(prebonding.frcData, addr) == 0x01)
                    if(nodeBonded)
                        bit0OK = 0xff;
                    else
                        notifyMainApp(EVT_FRC_DISABLE_PREBONDING_BIT0_ERR, 1);

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
                                    notifyMainApp(EVT_NODE_READ_MID, 1);
                                    prebonding.MIDlist[prebonding.MIDcount++] = prebonding.MID;          
                                    if(prebonding.MIDcount >= 127)
                                    {
                                        // Error, maximum prebonded nodes reached
                                        notifyMainApp(EVT_MAX_NODES_PREBONDED, 0);
                                        terminateProcess();
                                        return(ERR_MAX_NODES_PREBONDED);
                                    }
                                }
                            }
                        }
                    }
                    else
                        notifyMainApp(EVT_FRC_DISABLE_PREBONDING_BIT1_ERR, 1);
                }
            }
        }
        
        // Authorize prebonded nodes
        for(addr = 0; addr < prebonding.MIDcount; addr++)
        {
            prebonding.MID = prebonding.MIDlist[addr];
            if(checkDuplicitMID(addr) == 0x00)
            {
                // OK, Get next free address
                prebonding.nextAddr = nextFreeAddr(prebonding.nextAddr);
                if(prebonding.nextAddr == 0xff)
                {
                    // Error, no free address, terminate process
                    notifyMainApp(EVT_NO_FREE_ADDRESS, 0);
                    terminateProcess();
                    return(0xff);
                }            

                // Authorize node
                notifyMainApp(EVT_AUTHORIZE_BOND, 1);
                if(authorizeBond(prebonding.nextAddr, (unsigned short)prebonding.MIDlist[addr]) == DPA_OK);
                {
                    setBitValue(prebonding.newNodesMap, prebonding.nextAddr);
                    prebonding.newNode = 0xff;
                    notifyMainApp(EVT_AUTHORIZE_BOND_OK, 1);
                }
            }
            delayMS(10);
        }
        
        // Any new node ?
        if(prebonding.newNode)
        {
            // Send FRC command Prebonding to check the new nodes
            notifyMainApp(EVT_FRC_CHECK_NEW_NODES, 0);
            myDpaRequest.Extension.Plain.Data[0] = 0;       // Command 0 - Prebonding
            myDpaRequest.Extension.Plain.Data[1] = 0x01;
            myDpaRequest.Extension.Plain.Data[2] = 0x00;
            frcSend(3);
            
            // Check new nodes
            for(addr = 1; addr <= DPA_ADDRESS_MAX; addr++)
            {
                if(getBitValue(prebonding.newNodesMap, addr))
                {
                    // Bit0 is cleared (new node isn't responding to FRC) ?
                    if(getBitValue(prebonding.frcData, addr) == 0x00)
                    {
                        // No response from the node, remove the node
                        prebonding.param = addr;
                        notifyMainApp(EVT_COOR_REMOVING_BOND, 1);
                        removeBondedNode(addr);
                    }
                }
            }

            // Run Discovery
            for(discoveryRetry = ANparams.discoveryRetries; discoveryRetry != 0; discoveryRetry--)
            {
                notifyMainApp(EVT_DISCOVERY, 0);
                if(discovery(ANparams.discoveryTxPower, 0) == DPA_OK)
                    notifyMainApp(EVT_DISCOVERY_OK, 1);
                notifyMainApp(EVT_DISCOVERY_WAIT, 0);
                for( ; ; )
                {
                    // Wait for finish the Discovery
                     delayMS(1000);
                    // Terminate process ?
                    if(terminate == 0xff)
                    {
                        terminateProcess();
                        return(0xfe);
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
                for(addr = 1; addr <= DPA_ADDRESS_MAX; addr++)
                    if(getBitValue(networkInfo.discoveredNodesMap, addr) == 1)
                        networkInfo.discoveredNodesCount++;
                // Terminate process ?
                if(terminate == 0xff)
                {
                    terminateProcess();
                    return(0xfe);
                }
                if(networkInfo.discoveredNodesCount == networkInfo.bondedNodesCount)
                    break;
            }
        }
        else
            notifyMainApp(EVT_NO_NEW_NODE_PREBONDED, 0);
    }

    terminateProcess();
    return(0);
}