#include "user.h"                   
#include "autonetwork.h"

//-------------------------------
// User UART RX interrupt handler
//-------------------------------
void uartRXInterrupt(void)
{
    // Call DPA UART RX handler when a single byte is received
    MyDpaUartRxHandler();
}

//-------------------------------
// User UART TX interrupt handler
//-------------------------------
void uartTXInterrupt(void)
{
    // Call DPA UART RX handler
    MyDpaUartTxHandler();
}

//-----------------------------
// User timer interrupt handler
//-----------------------------
void userTimerInterrupt(void)
{
    // Provide 1ms tick for autonetwork
    autonetworkTick();
}

//--------------
// Main function
//--------------
int main(void)
{ 
    // Initialize user peripherals
    UserInit();

    // Application main loop
    while(1)
    {
        ProcessIO();        
    }
}

