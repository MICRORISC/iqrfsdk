AutoNetwork for C
=================

Introduction
------------

Algorithm for automatic creation of IQRF networks functionality is based on the idea 
of automatic adding new nodes into IQRF network without any human assistance. All 
phases of the process of addition new node into network is fully automatic, altough 
a human can control this process by setting values for the algorithm's parameters. 

The demo is based on libDPA library therefore it is recommened to look through 
libs-xxx/cLibDPA-xxx/!ReadMe.txt file before experimenting with the demo. 


Parts
-----

It consists of these parts:

- doc

	It contains a diagram with AutoNetwork algorithm for developers and log record from 
	running chipKIT ( Arduino ) example.

- examples

	- docklight

		A project for Docklight terminal [1] which can be used with the prepared example for 
		chipKIT ( Arduino ). The commands for START, STOP of the AutoNetwork algorithm and 
		REMOVE of bonded nodes are ready to be used.

		START: >S60,1,1,15,7\r

		where:
			60=temporaryAddressTimeout
 			1=authorizeRetries
 			1=discoveryRetries
			15=prebondingInterval
			7=TXpower

		STOP: >P\r

		REMOVE: >R\r

		( Broadcast to all nodes to remove their bonds and restart them and clear all bonds 
		 on coordinator )

	- chipkit ( arduino )

		A full project for chipKIT Uno32 or Arduino Leonardo.


Running
-------

1)

Before running the demo, make sure that you run DPA v2.2x on your DPA coordinator and DPA nodes. 
Also, make sure that CustomDpaHandler-Coordinator-AutoNetwork-xxx (for the coordinator) and 
CustomDpaHandler-AutoNetwork-xxx (for the nodes) are uploaded to the modules and custom 
handler is enabled in DPA configuration for the modules. Use IQRF IDE for these tasks.


2)

Available options:

a)

Acquire chipKIT or Arduino platform and use prepared full project in chipkit folder. You can
select the platform by prepared macros. The chipKIT platform is set by default. Use [2] to 
compile code for chipKIT platform or [3] for Arduino platform. The DPA coordinator can be 
connected to these platforms via IQRF breakout boards [4, 5].

#define UNO32
//#define LEONARDO

b)

Acquire any uC target board and use prepared template that is ready to be ported to your 
target board.


3)

The demo uses UART interface with 115200 baudrate to DPA coordinator by default. The second 
UART with 9600 baudrate is used as command interface to PC terminal such as Docklight. 

The interface to DPA coordinator can be easily changed to SPI if needed by prepared macro in 
the dpa_library.h file:

#define __SPI_INTERFACE__				// select for comunication via SPI
//#define __UART_INTERFACE__			// select for comunication via UART


References
----------

[1] http://docklight.de/
[2] http://chipkit.net/wpcproduct/mpide/
[3] https://www.arduino.cc/en/Main/Software
[4] http://iqrf.org/weben/index.php?sekce=products&id=iqrf-bb-01&ot=development-tools&ot2=development-kits
[5] http://iqrf.org/weben/index.php?sekce=products&id=iqrf-bb-02&ot=development-tools&ot2=development-kits  
