IQRF SDK - IQRF Software Development Kit
========================================

The IQRF SDK is a package which simplifies the integration of an IQMESH networks into C 
and Java applications.

It consists of these parts:

- jSimply
	A framework that allows to build java applications communicating with sensor networks. 
	It creates a structure to support many different technologies.
	Packages: 
		- simply-core-2.1.0.jar

- jSimply Asynchrony
	A framework extension supporting asynchronous communication.
	Packages:
		- simply-asynchrony-1.0.1.jar
		
- jSimply IQRF DPA
	A framework extension implementing basic means to support working with IQRF DPA 
	technology (data controlled wireless, without programming).
	Packages: 
		- simply-iqrf-2.0.1.jar
		- simply-iqrf-dpa-3.0.0.jar
		
- jSimply IQRF DPA v21x
	A framework extension with direct support of IQRF DPA 2.1x.
	Packages:
		- simply-iqrf-dpa-v21x-1.0.0.jar
		
- jSimply IQRF DPA v22x
	A framework extension with direct support of IQRF DPA 2.2x.
	Packages:
		- simply-iqrf-dpa-v22x-1.0.0.jar
		
- Libraries
	CDC, SPI, UART and UDP support. CDC is also available for C, C++ and SPI for C 
	programming language.
	
	Java packages:
		- simply-network-usbcdc-1.0.2.jar
		- simply-network-serial-v2-1.0.1.jar
		- simply-network-spi-1.0.1.jar
		- simply-network-udp-1.0.1.jar
		- hdlc-framing-v2-1.0.0.jar
		- jlibcdc-1.0.0.jar ( + CDCLib_JavaStub.dll, libCDCLib_JavaStub.so )
		- jlibrpi-io-2.0.0.jar ( + librpi_io_javastub.so )
		- jlibrpi-spi-1.0.0.jar
		- jlibrpi-spi-iqrf-1.0.0.jar ( + librpi_spi_iqrf_javastub.so )

	C (C++) packages:
		- libcdc-1.0.0.a ( also libs for Windows, C++ implementation )
		- clibcdc-1.0.0.a ( C wrapper for Linux )
		- clibrpi-io-2.0.0.a
		- clibrpi-spi-iqrf-1.0.0.a
		- clibdpa ( C library for uC without OS, it needs to be compiled (e.g. GCC) by the user )
		
- Demo software and documentation
	Demos:
		- cAutoNetwork ( It is based on cLibDPA for uC without OS )
		- jAutoNetwork ( It is based on jSimply framework )
		- jTemperatureRead ( It is based on jLibCDC, jLibRPI and jSimply )

	Comprehensive documentation and example programs.

- Source code
	Source code for all released for all libraries.
	

Overall directory structure for each library
-------------------------------------------- 

- doc
	Documentation. It consists of 2 parts: 
	- user documentation (if available)
		Currently is available in English language.
	- programming documentation
		In the form of Javadoc or Doxygen
	
- libs
	Jar packages and supporting libraries.

- sources
	Source code for all released .jar packages and supporting libraries.
	
- examples
	Examples of usage. There are 3 variants:
	- sources
		Only pure source files together with configuration files.
	- maven (or make)
		Maven or Makefile projects.

Dependencies
------------

Some parts of IQRF SDK are dependent on software of other parties. The software is 
public available so IQRF SDK doesn't contain it. This software is specified in the
'pom.xml' files. 

List of software, which some parts of IQRF SDK is dependent on, TRANSITIVE DEPENDENCIES ARE NOT INCLUDED:
* commons-configuration-1.8.jar
  download: http://repo1.maven.org/maven2/commons-configuration/commons-configuration/1.8/commons-configuration-1.8.jar

* commons-lang-2.6.jar
  download: http://repo1.maven.org/maven2/commons-lang/commons-lang/2.6/commons-lang-2.6.jar
  
* slf4j-api-1.7.6.jar
  download: http://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.6/slf4j-api-1.7.6.jar

* jssc-2.8.0.jar
  download: http://repo1.maven.org/maven2/org/scream3r/jssc/2.8.0/jssc-2.8.0.jar

* joda-time-2.6.jar
  download: http://repo1.maven.org/maven2/joda-time/joda-time/2.6/joda-time-2.6.jar

Only for examples:
* logback-classic-1.1.2.jar
  download: http://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.1.2/logback-classic-1.1.2.jar


Examples
--------

A set of sample applications are included in the IQRF SDK allowing a programmer to quickly 
understand the API and use it as part of a larger system. 

Five maven projects have been prepared for IQRF/DPA network:

	* jlibcdc-examples
  	  For IQRF USB CDC ( GW-USB-05 or CK-USB-04A ) 

	* jlibrpi-io-examples
  	  For IQRF SPI/IO extension board ( KON-RASP-01 )

	* jlibrpi-spi-iqrf-examples
  	  For IQRF SPI/IO extension board ( KON-RASP-01 )

	* jssc-examples
  	  For IQRF USB CDC UART ( CK-USB-04A )

	* judp-examples
  	  For IQRF UDP ( GW-ETH-02A or GW-WIFI-01 )

Two maven projects have been prepared for DPA network:

	* simply-iqrf-dpa-v21x-examples
  	  For IQRF DPA 2.1x
  
	* simply-iqrf-dpa-v22x-examples
  	  For IQRF DPA 2.2x

Three make projects have been prepared for IQRF/DPA network:

	* libcdc-examples-linux
	  clibcdc-examples-linux
	  libcdc-examples-win
	  For IQRF USB CDC ( GW-USB-05(06) or CK-USB-04A )

	* clibrpi-io-examples
	  For IQRF SPI/IO extension board ( KON-RASP-01 )

	* clibrpi-spi-examples
	  For IQRF SPI/IO extension board ( KON-RASP-01 )

Four make projects have been prepared for DPA network:

	* clibdpa-msp430f1611 ( via UART )
	  For IQRF SPI/UART/IO extension board ( IQRF-BB-02 )

	* clibdpa-msp430f2418 ( via SPI )
	  For IQRF SPI/UART/IO extension board ( IQRF-BB-02 )

	* clibdpa-chipKIT (via SPI/UART)
	  For IQRF SPI/UART/IO extension board ( IQRF-BB-02 )

	* clibdpa-arduino (via SPI/UART)
	  For IQRF SPI/UART/IO extension board ( IQRF-BB-01 )

* required HW ( For default configuration option - Simply Network USBCDC layer )

DS-DPA-01(02)
http://www.iqrf.org/weben/index.php?sekce=products&id=ds-dpa-01&ot=development-tools&ot2=development-sets
http://www.iqrf.org/weben/index.php?sekce=products&id=ds-dpa-02&ot=development-tools&ot2=development-sets

GW-USB-05(06) or CK-USB-04A ( Switching to CDC IQRF mode is a MUST to connect with IQRF SDK )
http://www.iqrf.org/weben/index.php?sekce=products&id=gw-usb-05&ot=gateways&ot2=gw-usb-05
http://www.iqrf.org/weben/index.php?sekce=products&id=gw-usb-06&ot=gateways&ot2=gw-usb-06
http://www.iqrf.org/weben/index.php?sekce=products&id=ck-usb-04a&ot=development-tools&ot2=development-kits

* required FW

Demo HWP v2.2x (v2.1x) or General HWP v2.2x (v2.1x)
http://www.iqrfalliance.org/index.php?section=members_zone#downl

( General HWP v2.2x (v2.1x) and CustomDpaHandler-UserPeripheral-V220 (V21x) example are required 
  in order to work with an example for the user peripherals. CustomDpaHandler-UserPeripheral-V220  
  (V21x) is part of released HWP pack. Examples for ADC a DALLAS 18B20 are also included in HWP
  pack. ) 


* required SW

IQRF IDE 4.xx
http://www.iqrf.org/weben/index.php?sekce=products&id=iqrf-ide-v400&ot=development-tools&ot2=development-sw

Java JDK 1.7
http://www.oracle.com/technetwork/java/javase/downloads/index.html

Embedded Java JDK 1.7 or higher ( Already included in the Raspbian distribution )

Netbeans 7.x or higher
https://netbeans.org/downloads/

MSP430-GCC ( msp430-gcc (GCC) 4.7.0 20120322 (mspgcc dev 20120716) ) for MSP430 platforms
http://contiki-os.blogspot.cz/2013/11/instant-contiki-27-available.html

UECIDE for chipKIT and Arduino platforms
http://uecide.org/download


* required SETUP (for jSimply framework examples)

In prior to running the IQRF SDK examples the IQMESH DPA network must be configured and setup. The user 
must first select DPA peripherals on each TR module as well as on GW-USB-05(06) ( CK-USB-04A + TR ) 
which acts as DPA coordinator. After that each TR module must be bonded to the coordinator 
GW-USB-05(06) ( CK-USB-04A + TR ). Both tasks can be nicely accomplished by the IQRF IDE.

Please, spend some time to get yourself familiar with basic concepts of Data Controlled Wireless Modules 
before diving into IQRF SDK.

Also prior to running of any example check your COM port number and set it accordingly in 
config/NetworkSettings.xml and modify config/PeripheralDistribution.xml according to the 
number of the nodes in your network and intended use of DPA peripheral.

Lastly check the settings of -Djava.library.path=src/main/resources/natives/x86 (x64) in the Netbeans 
Project properties/Run and set accordingly to your platform.


Version and versioning
----------------------

IQRF SDK is NOT a library itself, rather it is a CONTAINER of multiple libraries and artifacts. 
But in fact, each of the libraries has its own version. How to version a container of generally 
independent libraries?

We have decided to assign version number in a similar way as DPA framework follows. 
Example of IQRF SDK published on 31.08.2015: IQRF-SDK_210

Moreover, each library and demo folder is tagged with date of publication.
The example of library folder published on 31.08.2015: jSimply-150831


Repository
----------

Public repository with all released artefacts is available at: https://github.com/MICRORISC/iqrfsdk


Contact
-------

In case of any technical issue do not hesitate to contact us on the following email 
address: support@iqrf.org.

Enjoy!
IQRF SDK Team
