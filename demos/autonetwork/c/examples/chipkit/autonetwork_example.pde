/*
* Copyright 2015 MICRORISC s.r.o.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.f
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
#include <stdio.h>
#include "DPA_LIBRARY.h"
#include "AUTONETWORK.h"
#include "AUTONETWORK_Example.h"
// For ARDUINO BOARDS
//#include <SPI.h>
//#include <MsTimer2.h>

//=========================== defines =========================================
#define UNO32
//#define LEONARDO

#define SS 7

// For CHIPKIT BOARDS
#ifdef UNO32
/* Unlike many chipKIT/Arduino libraries, the DSPI library doesn't
** automatically instantiate any interface objects. It is necessary
** to declare an instance of one of the interface objects in the
** sketch. This creates an object to talk to SPI port 0. Similarly,
** declaring a variable of type DSPI1, DSPI2, DSPI3, etc. will create
** an object to talk to DSPI port 1, 2, or 3.
*/
#include <DSPI.h>
DSPI0 spi;
#endif
                   
//-----------------
// Global variables
//-----------------
#define SERIAL_BUFFER_SIZE  64
char serial_buffer_in[SERIAL_BUFFER_SIZE];
char serial_buffer_out[SERIAL_BUFFER_SIZE];

//----------------------------
// Application UART RX handler
//----------------------------
void MySerialEvent(void)
{
    uint8_t byte;

    while(Serial1.available())
    {
        // read received byte
        byte = Serial1.read();
    
        // load that byte to dpa library
        DPA_ReceiveUartByte(byte);
    }
} 

//-----------------
// DPA SPI transfer
//-----------------
uint8_t DPA_SendSpiByte(uint8_t Tx_Byte) 
{
    uint8_t Rx_Byte;    

#ifdef LEONARDO
    digitalWrite( SS, LOW );
    delayMicroseconds( 15 );

    SPI.beginTransaction(SPISettings(250000, MSBFIRST, SPI_MODE0));
    Rx_Byte = SPI.transfer( Tx_Byte );
    SPI.endTransaction();
  
    delayMicroseconds( 15 );
    digitalWrite( SS, HIGH );
#endif
#ifdef UNO32
    spi.setSelect(LOW);
    delayMicroseconds( 15 );

    spi.transfer(1, Tx_Byte, &Rx_Byte);

    delayMicroseconds( 15 );
    spi.setSelect(HIGH);
#endif
  
    return Rx_Byte;
}

//----------------------------
// DPA UART send byte function
//----------------------------
void DPA_SendUartByte(unsigned char Tx_Byte)
{      
    Serial1.write(Tx_Byte); 
}

//-------------------
// 1ms timer callback
//-------------------
#ifdef LEONARDO
void cb_timer1ms(void) 
#endif
#ifdef UNO32
uint32_t cb_timer1ms(uint32_t currentTime)
#endif
{
    // dpa timing
    DPA_SetTimmingFlag();	// set 1ms flag
    autonetworkTick();
#ifdef UNO32
    return (currentTime + CORE_TICK_RATE);
#endif
}

//-------------------------------------------
// Copy a string containing event description 
//-------------------------------------------
void getEventDescription(unsigned char index)
{
    // Autonetwork event description strings array, in this example placed in the CPU Flash memory
    // Event EVT_AN_PROCESS_STARTED 
    const char evt_00[] = {"Automatic network construction started\r\n\0"};
    // Event EVT_AN_PROCESS_STOPPED
    const char evt_01[] = {"Automatic network construction stopped\r\n\0"};
    // Event EVT_GET_NETWORK_INFO
    const char evt_02[] = {"Getting initial network info\r\n\0"};
    // Event EVT_ROUND_START
    const char evt_03[] = {"\r\nRound=%u, Nodes=%u, New nodes=%u\r\n\0"};
    // Event EVT_COOR_ENABLE_PREBONDING
    const char evt_04[] = {"Enable prebonding at coordinator, mask=%u, time=%u\r\n\0"};
    // Event EVT_COOR_DISABLE_PREBONDING
    const char evt_05[] = {"Disable prebonding at coordinator\r\n\0"};
    // Event EVT_COOR_READ_MID
    const char evt_06[] = {"Coordinator prebonded MID=%08lX, UserData=%04X\r\n\0"};
    // Event EVT_COOR_REMOVING_BOND
    const char evt_07[] = {"Removing node %u\r\n\0"};
    // Event EVT_NODE_ENABLE_PREBONDING
    const char evt_08[] = {"Enable prebonding at nodes, mask=%u, time=%u, LEDR=1\r\n\0"};
    // Event EVT_NODE_DISABLE_PREBONDING
    const char evt_09[] = {"Disable prebonding at nodes\r\n\0"};
    // Event EVT_NODE_READ_MID
    const char evt_10[] = {"Node %u prebonded MID=%08lX, UserData=%04X\r\n\0"};
    // Event EVT_AUTHORIZE_BOND
    const char evt_11[] = {"Authorizing node MID=%08lX, address %u\r\n\0"};
    // Event EVT_AUTHORIZE_BOND_OK 
    const char evt_12[] = {"OK, nodes count=%u\r\n\0"};
    // Event EVT_DISCOVERY 
    const char evt_13[] = {"Running discovery\r\n\0"};
    // Event EVT_DISCOVERY_WAIT
    const char evt_14[] = {"Waiting for coordinator to finish discovery\r\n\0"};
    // Event EVT_DISCOVERY_OK 
    const char evt_15[] = {"Discovered nodes=%u\r\n\0"};
    // Event EVT_WAIT_PREBONDING  
    const char evt_16[] = {"Waiting for prebonding for %u seconds\r\n\0"};
    // Event EVT_FRC_DISABLE_PREBONDING
    const char evt_17[] = {"Running FRC to disable and check for prebonding\r\n\0"};
    // Event EVT_FRC_DISABLE_PREBONDING_BIT0_ERR
    const char evt_18[] = {"Error @ FRC bit.0 is set, but node %u not bonded\r\n\0"};
    // Event EVT_FRC_DISABLE_PREBONDING_BIT1_ERR
    const char evt_19[] = {"Error @ FRC bit.1, set, but node %u is already bonded\r\n\0"};
    // Event EVT_FRC_CHECK_NEW_NODES
    const char evt_20[] = {"Running FRC to check new nodes and removing 0xFE nodes\r\n\0"};
    // Event EVT_NO_FREE_ADDRESS
    const char evt_21[] = {"No free address\r\n\0"};        
    // Event EVT_NO_NEW_NODE_PREBONDED
    const char evt_22[] = {"No new node prebonded\r\n\0"};
    // Event EVT_DPA_CONFIRMATION_TIMEOUT
    const char evt_23[] = {"DPA confirmation timeout\r\n\0"};
    // Event EVT_DPA_RESPONSE_TIMEOUT
    const char evt_24[] = {"DPA response timeout\r\n\0"};
    // Event EVT_REMOVE_ALL_BONDS
    const char evt_25[] = {"Remove all bonds at nodes and coordinator, restart nodes\r\n\0"};
    // Event EVT_MAX_NODES_PREBONDED
    const char evt_26[] = {"Maximum prebonded nodes reached\r\n\0"};
    // Event EVT_NODE_REMOTE_UNBOND
    const char evt_27[] = {"Remote unbonding node %u\r\n\0"};
    
    // Array of autonetwork description string pointers, must be defined in correct order from evt_00 to evt_nn
    const char *ptr_evt[] = {evt_00, evt_01, evt_02, evt_03, evt_04, evt_05, evt_06, evt_07, evt_08, evt_09, evt_10, evt_11, evt_12,
                             evt_13, evt_14, evt_15, evt_16, evt_17, evt_18, evt_19, evt_20, evt_21, evt_22, evt_23, evt_24, evt_25,
                             evt_26, evt_27};
    
    // Copy the event description to serial_buffer_out
    strcpy(serial_buffer_out, (char*)ptr_evt[index]);                   
}

//--------------------
// Autonetwork handler
//--------------------
void autonetworkHandler(unsigned char eventCode, T_AN_STATE *state)
{
    // Cpoy event description string to serial_buffer_out
    getEventDescription(eventCode);
    
    // Event with parameters ?
    if(state != NULL)
    {      
        // Yes, event with parameters            
        char buffer[SERIAL_BUFFER_SIZE];
        switch(eventCode)
        {
            case EVT_ROUND_START:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->round, state->newtorkInfo->bondedNodesCount, state->newtorkInfo->bondedNodesCount - state->prebondingInfo->origNodesCount);
                break;

            case EVT_COOR_ENABLE_PREBONDING:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->bondingMask, state->params->temporaryAddressTimeout);
                break;

            case EVT_NODE_ENABLE_PREBONDING:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->bondingMask, state->params->temporaryAddressTimeout);
                break;

            case EVT_WAIT_PREBONDING:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->delay);
                break;

            case EVT_COOR_READ_MID:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->MID, state->prebondingInfo->userData);
                break;

            case EVT_NODE_READ_MID:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->param, state->prebondingInfo->MID, state->prebondingInfo->userData);
                break;

            case EVT_AUTHORIZE_BOND:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->MID, state->prebondingInfo->nextAddr);
                break;

            case EVT_AUTHORIZE_BOND_OK:
            case EVT_FRC_DISABLE_PREBONDING_BIT0_ERR:
            case EVT_FRC_DISABLE_PREBONDING_BIT1_ERR:                
            case EVT_DISCOVERY_OK:
            case EVT_COOR_REMOVING_BOND:
            case EVT_NODE_REMOTE_UNBOND:
                sprintf(buffer, serial_buffer_out, state->prebondingInfo->param);
                break;

            // Unknown message
            default:
                strcpy(buffer, "Unknown event\r\n");
                break;
        }
        Serial.print(buffer);
    }
    else
    {
        // No parameters, send text
        Serial.print(serial_buffer_out);
    }  
}

//----------------------------------
// Check input from Serial interface
//----------------------------------
unsigned char checkInput(T_AN_PARAMS *AN_Params)
{
    static unsigned char offset = 0;
    unsigned char result = 0xff;

    // Any data available ?
    if (Serial.available() > 0)
    {

        // Prevent buffer overflow
        if(offset >= SERIAL_BUFFER_SIZE)
        {
            memset(serial_buffer_in, 0, SERIAL_BUFFER_SIZE);
            offset = 0;
        }
        
        // Read incomming character
        char c = Serial.read();
        
        // Backspace ?
        if(c == '\b')
        {
            // Yes
            if(offset > 0)
                offset--; 
            return(result);            
        }
        serial_buffer_in[offset++] = c;
        
        // End of command line ?
        if((c == '\r') || (c == '\n'))
        {
            // Check the first character is '>'
            if(serial_buffer_in[0] == '>')
            {
                // Process command
                switch(serial_buffer_in[1])
                {
                    // Start autonetwork command
                    case 'S':
                        // Check the pointer to T_AN_PARAMS
                        if(AN_Params != NULL)
                        {
                            // Get temporaryAddressTimeout, default value is 60 sec
                            AN_Params->temporaryAddressTimeout = atoi((char*)&serial_buffer_in[2]);
                            if(AN_Params->temporaryAddressTimeout == 0)
                                AN_Params->temporaryAddressTimeout = 60;
                               
                            // Get authorizeRetries, default value is 1
                            char *ptr = strchr((char*)serial_buffer_in, ',');
                            if(ptr == NULL)
                                goto SYNT_ERR;                              
                            ptr++;
                            AN_Params->authorizeRetries = atoi(ptr);
                            if(AN_Params->authorizeRetries == 0)
                                AN_Params->authorizeRetries = 1;
                            
                            // Get discoveryRetries, default value is 1
                            ptr = strchr(ptr, ',');
                            if(ptr == NULL)
                                goto SYNT_ERR;                            
                            ptr++;
                            AN_Params->discoveryRetries = atoi(ptr);
                            if(AN_Params->discoveryRetries == 0)
                                AN_Params->discoveryRetries = 1;                            
                            
                            // Get prebondingInterval, default value is 15 sec.
                            ptr = strchr(ptr, ',');
                            if(ptr == NULL)
                                goto SYNT_ERR;                            
                            ptr++;
                            AN_Params->prebondingInterval = atoi(ptr);
                            if(AN_Params->prebondingInterval < 15)
                                AN_Params->prebondingInterval = 15;                              
                            
                            // Get discoveryTxPower, default value is 0
                            ptr = strchr(ptr, ',');
                            if(ptr == NULL)
                                goto SYNT_ERR;
                            ptr++;
                            AN_Params->discoveryTxPower = atoi(ptr);
                            if(AN_Params->discoveryTxPower > 7)
                                AN_Params->discoveryTxPower = 0;                            
                            result = CMD_START_AN;
                        }
                        break;
  
                    // Stop autonetwork command
                    case 'P':
                        result = CMD_STOP_AN;
                        break;
  
                    // Remove all bonds command
                    case 'R':
                        result = CMD_REMOVE_BONDS;
                        break;  
  
                    // Unknown command
                    default:
                        result = 0xff;
                        Serial.print("Unknown command\r\n");
                        break;
                }
            }
            else
            {
SYNT_ERR:              
                Serial.print("Syntax error\r\n");
            }

            // Prepare for next command
            memset(serial_buffer_in, 0, SERIAL_BUFFER_SIZE);
            offset = 0;
        }
    }

    return(result);
}

//---------------
// Initialization
//---------------
void setup()
{
    // Up - PC
    Serial.begin(9600);
    
    // Down - DCTR 
#ifdef __UART_INTERFACE__
    Serial1.begin(115200);
#endif

#ifdef __SPI_INTERFACE__
#ifdef LEONARDO
    pinMode( SS, OUTPUT );
    SPI.begin();
#endif
#ifdef UNO32
//    spi.setPinSelect(SS);
    spi.begin(SS);
    spi.setSpeed(250000);
#endif
#endif
    
    //timer 1ms
#ifdef LEONARDO
    MsTimer2::set(1, cb_timer1ms);
    MsTimer2::start();
#endif
#ifdef UNO32
    attachCoreTimerService(cb_timer1ms);
#endif
    
    // Init autonetwork, register callback functions
    autonetworkInit(autonetworkHandler, checkInput);
    
    // Print help
#ifdef __SPI_INTERFACE__      
    Serial.println("Peripheral and DPA (SPI interface) init done\r\n");
#endif      
#ifdef __UART_INTERFACE__      
    Serial.println("Peripheral and DPA (UART interface @115200 Bd) init done\r\n");
#endif      
    Serial.println("Supported commands:");
    Serial.println("1. Remove all bonds and reset bonds");
    Serial.println("   >R<CR>");
    Serial.println("2. Start autonetwork");
    Serial.println("   >SP1,P2,P3,P4,P5<CR>");
    Serial.println("   P1 is Temporary address timeout");    
    Serial.println("   P2 is Authorize retries");    
    Serial.println("   P3 is Discovery retries");    
    Serial.println("   P4 is Prebonding interval");    
    Serial.println("   P5 is Discovery TX power");    
    Serial.println("   Example: >S60,1,1,35,0<CR>");  
    Serial.println("3. Stop autonetwork");
    Serial.println("   >P<CR>\r\n");  
}

//------------
// Application
//------------
void loop()
{  
    memset(serial_buffer_in, 0, 64);   
    for (;;)
    {      
        // Check input from Serial interface
        T_AN_PARAMS params;
        switch(checkInput(&params))
        {
            // Start autonetwork command
            case CMD_START_AN:                
                autonetwork(&params);
                break;

            // Remove all bonds at nodes and coordinator command
            case CMD_REMOVE_BONDS:
                removeAllBonds();                
                break;                            
        }      
    }
}


