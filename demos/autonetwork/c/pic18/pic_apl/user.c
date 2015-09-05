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
#include "usb_config.h"
#include "autonetwork.h"

//------------------------------
// Autonetwork event description
//------------------------------
ROM char text[][64] =  {
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

#pragma udata  
unsigned char usb_rx_buf[64];
unsigned char input;
T_AN_PARAMS params;
char buffer[64];

//---------------------
//SetLineCoding handler
//---------------------
void MySetLineCodingHandler(void)
{
    //Report current settings to USB Host
    CDCSetBaudRate(line_coding.dwDTERate.Val);
    CDCSetCharacterFormat(line_coding.bCharFormat);
    CDCSetParity(line_coding.bParityType);
    CDCSetDataSize(line_coding.bDataBits);
}

//--------------------
// Autonetwork handler
//--------------------
void autonetworkHandler(unsigned char eventCode, T_AN_STATE *state)
{
    if((USBDeviceState < CONFIGURED_STATE) || (USBSuspendControl == 1))
        return;

    memset(buffer, 0, 64);
    if(mUSBUSARTIsTxTrfReady())
    {
        // Event with parameters ?
        if(state != NULL)
        {
            // Yes            
            switch(eventCode)
            {
                case EVT_ROUND_START:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->round, state->newtorkInfo->bondedNodesCount, state->newtorkInfo->bondedNodesCount - state->prebondingInfo->origNodesCount);
                    break;

                case EVT_COOR_ENABLE_PREBONDING:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->bondingMask, state->params->temporaryAddressTimeout);
                    break;

                case EVT_NODE_ENABLE_PREBONDING:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->bondingMask, state->params->temporaryAddressTimeout);
                    break;

                case EVT_WAIT_PREBONDING:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->delay);
                    break;

                case EVT_COOR_READ_MID:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->MID, state->prebondingInfo->userData);
                    break;

                case EVT_NODE_READ_MID:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->param, state->prebondingInfo->MID, state->prebondingInfo->userData);
                    break;

                case EVT_AUTHORIZE_BOND:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->MID, state->prebondingInfo->nextAddr);
                    break;

                case EVT_AUTHORIZE_BOND_OK:
                case EVT_FRC_DISABLE_PREBONDING_BIT0_ERR:
                case EVT_FRC_DISABLE_PREBONDING_BIT1_ERR:                
                case EVT_DISCOVERY_OK:
                case EVT_COOR_REMOVING_BOND:
                    sprintf(buffer, (ROM char*)text[eventCode], state->prebondingInfo->param);
                    break;

                // Unknown message
                default:
                    putrsUSBUSART((ROM char*)"Unknown event\r\n");
                    break;
            }
            putsUSBUSART(buffer);
        }
        else
        {
            // No parameters, send text
            putrsUSBUSART(text[eventCode]);
        }

        while(USBUSARTIsTxTrfReady() == 0)
            CDCTxService();
    }
}

//-----------------------
// Check USB CDC transfer
//-----------------------
unsigned char checkInput(void *parameters)
{
    static unsigned char offset = 0;
    char *ptr;
    T_AN_PARAMS *par;
    unsigned char result = 0xff, inputDataLen;

    if((USBDeviceState < CONFIGURED_STATE) || (USBSuspendControl == 1))
        return(0xff);

    if(mUSBUSARTIsTxTrfReady())
    {
        inputDataLen = getsUSBUSART((char*)&usb_rx_buf[offset], 0x40 - offset);
        if(inputDataLen > 0)
        {
            ptr = strchr((char*)usb_rx_buf, '\r');
            if(ptr != NULL)
            {
                if(usb_rx_buf[0] == '>')
                {
                    switch(usb_rx_buf[1])
                    {
                        // Start autonetwork command
                        case 'S':
                            par = (T_AN_PARAMS*)parameters;
                            par->temporaryAddressTimeout = atoi((char*)&usb_rx_buf[2]);
                            ptr = strchr((char*)usb_rx_buf, ',');
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
                            result = 0xff;
                            putrsUSBUSART((ROM char*)"Unknown command\r\n");
                            break;
                    }
                }
                else
                    putrsUSBUSART((ROM char*)"Syntax error\r\n");
                memset(usb_rx_buf, 0, 64);
                offset = 0;
            }
            else
                offset = strlen((char*)usb_rx_buf);
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
        
    if((USBDeviceState < CONFIGURED_STATE) || (USBSuspendControl == 1))
        return;        
    
    CDCTxService();
}

//--------------------------------------------
//Init Timer4 (tick) and Timer0 (UART RX Tout)
//---------------------------------------------
void TickInit(void)         
{
    //Timer4 - 1 ms tick generator
    T4CON = 0x76;
    PR4 = 50;
    IPR3bits.TMR4IP = 0;
    PIR3bits.TMR4IF = 0;    //Clear flag
    PIE3bits.TMR4IE = 1;    //Enable interrupt
}

//----------------
//Init application
//----------------
void UserInit(void)
{
    //TRISA and LATA
    TRISA = 0b00010001;
    LATA  = 0b00000000;
    //Set TRISB and TRISC for UART
    TRISB = 0b00111001;
    LATB  = 0b00111000;
    TRISC = 0b11111100;
    LATC  = 0b00000010;

    //Set PPS for UART
    RPINR16 = 13;           //RC2 - UART2 RX (input)
    RPOR12 = 5;             //RC1 - UART2 TX (output)

    //PPS settings
    //!!! Check SPI setting in HardwareProfile.h upon PPS change!!!
    EECON2 = 0x55;              //Lock sequence
    EECON2 = 0xAA;
    PPSCONbits.IOLOCK = 1;

    //Initialize UART - 57600 Bd, 8bit, no parity
    uartInit();
   
    //Enable interrupts
    INTCON  = 0xc0;		//GIE=1, PEIE=1
    INTCON2 = 0x00;		//RB pullup ON
    INTCON3 = 0x00;
    RCONbits.IPEN = 1;          //Enable priority levels on  interrupts

    //Initialize Timer4, Timer0
    TickInit();
    
    autonetworkInit();
}