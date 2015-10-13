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

#ifndef __AUTONETWORK__
#define __AUTONETWORK__

//-------- ----------
// DPA implementation
//-------- ----------
#define NO_WAITING              0
#define CONFIRMATION_WAITING	1
#define RESPONSE_WAITING        2
#define DPA_OK                  4
#define DPA_ERROR               8
#define DPA_TIMEOUT             128
#define DPA_ADDRESS_MAX         239
#define DPA_ADDRESS_TEMP        254
#define DPA_ADDRESS_BROADCAST   255
#define DPA_ADDRESS_COORD       0

//----------------------
// Autonetwork constants
//----------------------
#define AN_MIN_PREBONDING_TIME 15

//------------------------
// Autonetwork event codes
//------------------------
#define EVT_AN_PROCESS_STARTED              0x00
#define EVT_AN_PROCESS_STOPPED              0x01
#define EVT_GET_NETWORK_INFO                0x02
#define EVT_ROUND_START                     0x03
#define EVT_COOR_ENABLE_PREBONDING          0x04
#define EVT_COOR_DISABLE_PREBONDING         0x05
#define EVT_COOR_READ_MID                   0x06
#define EVT_COOR_REMOVING_BOND              0x07
#define EVT_NODE_ENABLE_PREBONDING          0x08
#define EVT_NODE_DISABLE_PREBONDING         0x09
#define EVT_NODE_READ_MID                   0x0a
#define EVT_AUTHORIZE_BOND                  0x0b
#define EVT_AUTHORIZE_BOND_OK               0x0c
#define EVT_DISCOVERY                       0x0d
#define EVT_DISCOVERY_WAIT                  0x0e
#define EVT_DISCOVERY_OK                    0x0f
#define EVT_WAIT_PREBONDING                 0x10
#define EVT_FRC_DISABLE_PREBONDING          0x11
#define EVT_FRC_DISABLE_PREBONDING_BIT0_ERR 0x12
#define EVT_FRC_DISABLE_PREBONDING_BIT1_ERR 0x13
#define EVT_FRC_CHECK_NEW_NODES             0x14
#define EVT_NO_FREE_ADDRESS                 0x15
#define EVT_NO_NEW_NODE_PREBONDED           0x16
#define EVT_DPA_CONFIRMATION_TIMEOUT        0x17
#define EVT_DPA_RESPONSE_TIMEOUT            0x18
#define EVT_REMOVE_ALL_BONDS                0x19
#define EVT_MAX_NODES_PREBONDED             0x1a
#define EVT_NODE_REMOTE_UNBOND              0x1b
#define EVT_WITH_PARAM                      0x01
#define EVT_WITHOUT_PARAM                   0x00

//-------------------------
// Autonetwork return codes
//-------------------------
#define ERR_PROCESS_TERMINATED              0xff
#define ERR_NO_FREE_ADDRESS                 0xfe
#define ERR_MAX_NODES_PREBONDED             0xfd
#define AN_OK                               0x00
#define AN_ERROR                            0xff

//--------------------
// Buffer size, offset
//--------------------
#define NODE_BITMAP_SIZE                    32        // Bitmap for 256 nodes
#define MID_BUFFER_SIZE                     128       // Buffer for 128 MIDs 
#define FRC_BUFFER_SIZE                     64
#define FRC_DATA_OFFSET                     0
#define FRC_DATA_SIZE                       55
#define FRC_EXTRA_DATA_OFFSET               55
#define FRC_EXTRA_DATA_SIZE                 9

//-----------------------
// Autonetwork parameters
//-----------------------
typedef struct
{
    unsigned short prebondingInterval;
    unsigned short discoveryTxPower;
    unsigned short authorizeRetries;
    unsigned short discoveryRetries;
    unsigned short temporaryAddressTimeout;
}T_AN_PARAMS;

//-------------------------
// Enable bonding structure
//-------------------------
typedef struct
{
    unsigned char BondingMask;
    unsigned char Control;
    unsigned short UserData;
} TPerCoordinatorNodeEnableRemoteBonding_Request;

//-------------
// Network info
//-------------
typedef struct
{
    unsigned char bondedNodesMap[NODE_BITMAP_SIZE];
    unsigned char bondedNodesCount;
    unsigned char discoveredNodesMap[NODE_BITMAP_SIZE];
    unsigned char discoveredNodesCount;
    unsigned char DID;
}T_NETWORK_INFO;

//----------------
// Prebonding info
//----------------
typedef struct
{
    unsigned long MIDlist[MID_BUFFER_SIZE];
    unsigned char MIDcount;
    unsigned long MID;
    unsigned short userData;
    unsigned char newNodesMap[NODE_BITMAP_SIZE];
    unsigned char frcData[FRC_BUFFER_SIZE];
    unsigned char round;
    unsigned char param;
    unsigned char origNodesCount;
    unsigned char bondingMask;
    unsigned char nextAddr;
    unsigned char newNode;
    unsigned char discoveredNodesCount;
    unsigned short wTimeout;
    unsigned short waitBonding;
    unsigned short waitBonding10ms;
    unsigned short delay;
}T_PREBONDING_INFO;

//------------------
// Autonetwork state
//------------------
typedef struct
{
    T_NETWORK_INFO *newtorkInfo;
    T_PREBONDING_INFO *prebondingInfo;
    T_AN_PARAMS *params;
}T_AN_STATE;

//-------------------------------
// Autonetwork callback functions
//-------------------------------
typedef void (*autonetworkNotify_CB)(unsigned char eventCode, T_AN_STATE *state);
typedef unsigned char (*autonetworkCheckInput_CB)(T_AN_PARAMS *AN_Params);

//-----------
// Prototypes
//-----------
void autonetworkTick(void);
void autonetworkInit(autonetworkNotify_CB notify_CB, autonetworkCheckInput_CB checkInput_CB);
unsigned char autonetwork(T_AN_PARAMS *par);
void removeAllBonds(void);
unsigned char ledR(unsigned short addr, unsigned char cmd);

#endif
