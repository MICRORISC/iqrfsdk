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

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "user.h"
#include "uart.h"
#include "autonetwork.h"

//------------------------------
// Autonetwork event description
//------------------------------
char text[][64] = {
                            "Automatic network construction started\r\n\0",
                            "Automatic network construction stopped\r\n\0",
                            "Getting initial network info\r\n\0",
                            "\r\nRound=%u, Nodes=%u, New nodes=%u\r\n\0",
                            "Enable prebonding at coordinator, mask=%u, time=%u\r\n\0",
                            "Disable prebonding at coordinator\r\n\0",
                            "Coordinator prebonded MID=%08lX, UserData=%04X\r\n\0",
                            "Removing node %u\r\n\0",
                            "Enable prebonding at nodes, mask=%u, time=%u, LEDR=1\r\n\0",
                            "Disable prebonding at nodes\r\n\0",
                            "Node %u prebonded MID=%08lX, UserData=%04X\r\n\0",
                            "Authorizing node MID=%08lX, address %u\r\n\0",
                            "OK, nodes count=%u\r\n\0",
                            "Running discovery\r\n\0",
                            "Waiting for coordinator to finish discovery\r\n\0",
                            "Discovered nodes=%u\r\n\0",
                            "Waiting for prebonding for %u seconds\r\n\0",
                            "Running FRC to disable and check for prebonding\r\n\0",
                            "Error @ FRC bit.0 is set, but node %u not bonded\r\n\0",
                            "Error @ FRC bit.1, set, but node %u is already bonded\r\n\0",
                            "Running FRC to check new nodes\r\n\0",
                            "No free address\r\n\0",
                            "No new node prebonded\r\n\0",
                            "DPA confirmation timeout\r\n\0",
                            "DPA response timeout\r\n\0",
                            "Remove all bonds at nodes and coordinator, reset nodes\r\n\0",
                            "Maximum prebonded nodes reached\r\n\0"
                    };

char inputBuffer[64];
unsigned char input;
T_AN_PARAMS params;
char buffer[64];

//-----------------------------------------
// Function to get data from user interface
//-----------------------------------------
unsigned char userInterfaceGet(void)
{
    unsigned char len;
    
    // Data is saved in input buffer
    // len contains the number of bytes available in inputBuffer
    
    return(len);
}

//---------------------------------------
// Function to put data to user interface
//---------------------------------------
void userInterfacePut(char *string)
{
    // Parameter string points to null terminated string containing a message
}

//--------------------
// Autonetwork handler
//--------------------
void autonetworkHandler(unsigned char eventCode, T_AN_STATE *state)
{
    // Event with parameters ?
    if(state != NULL)
    {
        // Yes, format string and send it over user interface            
        switch(eventCode)
        {
            case EVT_ROUND_START:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->round, state->newtorkInfo->bondedNodesCount, state->newtorkInfo->bondedNodesCount - state->prebondingInfo->origNodesCount);
                break;

            case EVT_COOR_ENABLE_PREBONDING:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->bondingMask, state->params->temporaryAddressTimeout);
                break;

            case EVT_NODE_ENABLE_PREBONDING:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->bondingMask, state->params->temporaryAddressTimeout);
                break;

            case EVT_WAIT_PREBONDING:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->delay);
                break;

            case EVT_COOR_READ_MID:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->MID, state->prebondingInfo->userData);
                break;

            case EVT_NODE_READ_MID:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->param, state->prebondingInfo->MID, state->prebondingInfo->userData);
                break;

            case EVT_AUTHORIZE_BOND:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->MID, state->prebondingInfo->nextAddr);
                break;

            case EVT_AUTHORIZE_BOND_OK:
            case EVT_FRC_DISABLE_PREBONDING_BIT0_ERR:
            case EVT_FRC_DISABLE_PREBONDING_BIT1_ERR:                
            case EVT_DISCOVERY_OK:
            case EVT_COOR_REMOVING_BOND:
                sprintf(buffer, (char*)text[eventCode], state->prebondingInfo->param);
                break;

            // Unknown message
            default:
                userInterfacePut((char*)"Unknown event\r\n");
                return;            
        }
        userInterfacePut(buffer);
    }
    else
    {
        // No parameters, send text
        userInterfacePut(text[eventCode]);
    }
}

//-------------------------------------------------------
// Check input from user interface to control autonetwork
//-------------------------------------------------------
unsigned char checkInput(void *parameters)
{
    char *ptr;
    T_AN_PARAMS *par;
    unsigned char result = 0xff, inputDataLen;

    // Data parsing Example 
    
    inputDataLen = userInterfaceGet();
    if(inputDataLen > 0)
    {
        ptr = strchr((char*)inputBuffer, '\r');
        if(ptr != NULL)
        {
            if(inputBuffer[0] == '>')
            {
                switch(inputBuffer[1])
                {
                    // Start autonetwork command
                    case 'S':
                        par = (T_AN_PARAMS*)parameters;
                        par->temporaryAddressTimeout = atoi((char*)&inputBuffer[2]);
                        ptr = strchr((char*)inputBuffer, ',');
                        ptr++;
                        par->authorizeRetries = atoi(ptr);
                        ptr = strchr(ptr, ',');
                        ptr++;
                        par->discoveryRetries = atoi(ptr);
                        ptr = strchr(ptr, ',');
                        ptr++;
                        par->prebondingInterval = atoi(ptr);
                        ptr = strchr(ptr, ',');
                        ptr++;
                        par->discoveryTxPower = atoi(ptr);
                        result = CMD_START_AN;
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
                        userInterfacePut((char*)"Unknown command\r\n");
                        return(0xff);
                }
            }
            userInterfacePut(buffer);
        }
        else
        {
            userInterfacePut((char*)"Syntax error\r\n");
        }
    }
    return(result);
}

//------------------
//ProcessIO function
//------------------
void ProcessIO(void)
{
    input = checkInput((void*)&params);
    switch(input)
    {
        // Start autonetwork
        case CMD_START_AN:
            autonetwork(&params);
            break;

        // Remove all bonds at nodes and coordinator
        case CMD_REMOVE_BONDS:
            removeAllBonds();
            break;
    }
        
}

//--------------------------------------------
//Init Timer4 (tick) and Timer0 (UART RX Tout)
//---------------------------------------------
void TickInit(void)         
{
    // User code to initialize a timer providing 1 ms interrupt
}

//----------------
//Init application
//----------------
void UserInit(void)
{
    // User code to initialize peripherals
    TickInit();
    uartInit();
    autonetworkInit();
}
