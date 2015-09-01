//******************************************************************************
// HW profile, modify for your target
//****************************************************************************** 
#ifndef __HARDWARE_PROFILE_H
#define __HARDWARE_PROFILE_H

#define PIC18F26J50
#define CLOCK_FREQ 48000000
#define GetSystemClock()  CLOCK_FREQ   
#define GetInstructionClock() CLOCK_FREQ   

//** LED ************************************************************
#define LED1_TRIS			(TRISCbits.TRISC0)      // LED1 (OUT)
#define LED1_IO				(LATCbits.LATC0)

#define LED2_TRIS			(TRISBbits.TRISB2)      // LED2 (OUT)
#define LED2_IO				(LATBbits.LATB2)
	
//** BUTTON *********************************************************
#define BTN1_TRIS			(TRISBbits.TRISB4)      // Button 1 (IN)
#define BTN1_IO				(PORTBbits.RB4)
	
#define BTN2_TRIS			(TRISBbits.TRISB5       // Button 2 (IN)
#define BTN2_IO				(PORTBbits.RB5)

//** OTHERS *********************************************************
#define PWR_TR_TRIS			(TRISBbits.TRISB1)	// TR module power control (OUT)
#define PWR_TR_IO			(LATBbits.LATB1)

#define OVER_CURRENT_TRIS   (TRISAbits.TRISA0)	// AP2552 over-current indicator output, active in low (IN)
#define OVER_CURRENT_IO		(PORTAbits.RA0)
  
//** TR SPI *********************************************************		
#define CS_TR_TRIS			(TRISBbits.TRISB3)	// CS for TR module (OUT)
#define CS_TR_IO			(LATBbits.LATB3)
	
#define TR_SCK_TRIS			(TRISBbits.TRISB0)	// (OUT)
#define TR_SCK_IO			(LATBbits.LATB0)
	
#define TR_SDO_TRIS			(TRISCbits.TRISC7)	// (OUT)
#define TR_SDO_IO			(LATCbits.LATC7)
	
#define TR_SDI_TRIS			(TRISCbits.TRISC6)	// (IN)
#define TR_SDI_IO			(PORTCbits.RC6)

// TR module
#define TR_SPI_IF			PIR3bits.SSP2IF
#define TR_SSPBUF			SSP2BUF
#define TR_SPICON1			SSP2CON1
#define TR_SPICON1bits                  SSP2CON1bits
#define TR_SPISTAT			SSP2STAT
#define TR_SPISTATbits                  SSP2STATbits
#define SPIEN				SSPEN
	
//*******************************************************************	
// nepouzito
//#define USE_SELF_POWER_SENSE_IO
#define tris_self_power     TRISAbits.TRISA2                    // Input
#if defined(USE_SELF_POWER_SENSE_IO)
#define self_power          PORTAbits.RA2
#else
#define self_power          1
#endif

//#define USE_USB_BUS_SENSE_IO                                  //JP1 must be in R-U position to use this feature on this board
#define tris_usb_bus_sense  TRISBbits.TRISB5                    // Input
#if defined(USE_USB_BUS_SENSE_IO)
#define USB_BUS_SENSE       PORTBbits.RB5
#else
#define USB_BUS_SENSE       1
#endif

#endif


