Introduction
------------

The LibDPA library intended usage is to provide programming language means for communication 
between uC and TR module, equipped with the firmware supporting the DPA framework.
  
The library implementation is based on encapsulation of DPA commands, sent from uC to TR module, 
into programming language's functions. More detailed information about DPA framework and commands 
are in document "IQRF DPA framework, Technical Guide" ( in IQRF Startup package ).

These boards [1,2] can be used to interface DCTR modules to a control unit.


Versions
--------

The library is available in two versions. 

The version v1 is initial version and supports DPA framework v2.01, v2.1x and v2.2x.

The version v2 has been reworked a little and integrates header DPA.h with complete definition of 
all data structures. DPA.h is taken from the distribution of DPA framework and allows for easier
integration of DPA into external uC. At the same time it allows us to handle the update to the 
next versions of DPA framework more easily. It is available for DPA framework v2.2x.  


Features
--------

- intended for communication with DCTR-72(52) modules
- intended for both DPA coordinator and node
- supported DPA frameworks: 2.01, 2.1x, 2.2x
- supported communication interfaces: SPI, UART
- supported programming languages: C for uC
- supported operating systems: None
- lightweight and easy to use
- well documented with examples for SPI and UART


Integration
-----------

The pointer to struct T_DPA_PACKET is used for communication between user's application and the library. 
The definition of T_DPA_PACKET can be found in the file dpa_library.h. If the user wishes to use the 
services of the library the files dpa_library.c(h) must be included in user's project and following
conditions must be met.

	- select the communication interface in the library header file
		
		#define __SPI_INTERFACE__
		//#define __UART_INTERFACE__

	- select the version of DPA framework in the library header file
		
		//#define __DPA_LIB_VER_2_0x
		//#define __DPA_LIB_VER_2_1x
		#define __DPA_LIB_VER_2_2x

	- implement functions to transfer of 1B to TR module via selected communication interface
		
		SPI: 	UINT8 DPA_SendSpiByte(UINT8 Tx_Byte)
		UART: 	void DPA_SendUartByte(UINT8 Tx_byte)
				void DPA_ReceiveUartByte(UINT8 Rx_byte)

	- call the function void DPA_SetTimmingFlag(void) with 1ms period. It is recommended to call
	  the function in the interrupt.

	- call the function void DPA_LibraryDriver(void) in the main loop of the user's application.
	  The calling period is not strictly defined. If the function is called in the interrupt then
	  it is necessary to bear in mind that the function takes time at least of 1B transfer via 
	  selected interface.

	- initialize the library by calling following functions first:
		
		void DPA_Init(void)
		void DPA_SetAnswerHandler(T_DPA_ANSWER_HANDLER dpaAnswerHandler)
			dpaAnswerHandler is user's function which is called by the library after packet 
			reception from DPA framework.


API functions
-------------

- void DPA_Init(void)
- DPA_SetAnswerHandler(T_DPA_ANSWER_HANDLER dpaAnswerHandler)
- DPA_SetTimmingFlag(void)
- void DPA_LibraryDriver(void)
	
	The brief description of these functions is in the paragraph Integration.

- void DPA_SendRequest(T_DPA_PACKET *dpaRequest, UINT8 dataSize)
	
	The function sends DPA request to TR module via selected interface. The user fills the 
	T_DPA_PACKET struct and defines size of additional data (if any) in the DPA request. 
	By additional data are meant bytes which follows after DPA request header NAdr, PNum, 
	PCmd and HwProfile. Some DPA requests require the additional data.     

- UINT16 DPA_GetEstimatedTimeout(void)
	
	The function returns estimated time for delivery of DPA response for sent DPA request.
	The timeout is calculated from the bytes in DPA confirmation and is in miliseconds.

- DPA_GetStatus()
	
	The function returns a state in which the library is found. It is recommended to call
	this macro before calling DPA_SendRequest(...).

	DPA_READY - the library is ready for new requests
	DPA_BUSY  - the library is processing the request 


References
----------

[1] http://iqrf.org/weben/index.php?sekce=products&id=iqrf-bb-01&ot=development-tools&ot2=development-kits
[2] http://iqrf.org/weben/index.php?sekce=products&id=iqrf-bb-02&ot=development-tools&ot2=development-kits
