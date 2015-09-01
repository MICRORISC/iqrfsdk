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

//-------------------------
// Autonetwork return codes
//-------------------------
#define ERR_PROCESS_TERMINATED              0xff
#define ERR_NO_FREE_ADDRESS                 0xfe
#define ERR_MAX_NODES_PREBONDED             0xfd

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
    unsigned char bondedNodesMap[32];
    unsigned char bondedNodesCount;
    unsigned char discoveredNodesMap[32];
    unsigned char discoveredNodesCount;
    unsigned char DID;
}T_NETWORK_INFO;

//----------------
// Prebonding info
//----------------
typedef struct
{
    unsigned long MIDlist[128];
    unsigned char MIDcount;
    unsigned long MID;
    unsigned short userData;
    unsigned char newNodesMap[32];
    unsigned char frcData[64];
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

void MyDpaUartRxHandler(void);
void MyDpaUartTxHandler(void);
void autonetworkTick(void);
void autonetworkInit(void);
unsigned char autonetwork(T_AN_PARAMS *par);
void removeAllBonds(void);

#endif