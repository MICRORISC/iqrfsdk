IQRFSDK
=======

Use IQRF wireless with standard devices. Simply.
Libraries and tools enabling you to easily build control software for your wireless application.

Part - Libs
===========

Libs contain simple standalone libraries and examples e.g. write/read for multiple communication
interfaces such as SPI/UART/CDC/UDP. Multiple platforms are supported. C version of the libraries
and examples is coming.  


arduino/spi
-----------

IQRF SPI driver for Arduino UNO


gemalto/spi
-----------

IQRF SPI driver for Gemalto M2M modules


iqrf/cdc-uart
-------------

IQRF Serial (DPA UART) examples


iqrf/cdc-iqrf
-------------

IQRF CDC library and examples


iqrf/udp
--------

IQRF UDP examples


mcu/spi-uart
------------

IQRF DPA SPI and UART library for uC without OS


raspberry/io-spi
----------------

IQRF IO-SPI driver and examples


Part - Simply
=============

SDK framework Simply brings together all standalone libraries and creates unified access method
to IQRF mesh network via multiple communication interfaces. IQRF DPA protocol is fully implemented
and users are provided with method calls to invoke functionality in the wireless network.    


simply-asynchrony
-----------------

SDK framework support for asynchronous messages


simply-core
-----------

SDK framework main structure


simply-iqrf
-----------

SDK framework support for IQRF types


simply-iqrf-dpa-v201
--------------------

SDK framework support for DPA protocol v2.01


simply-iqrf-dpa-v201-examples
-----------------------------

SDK framework basic examples for DPA protocol v2.01


simply-iqrf-dpa-v210
--------------------

SDK framework support for DPA protocol v2.10


simply-iqrf-dpa-v210-examples
-----------------------------

SDK framework basic examples for DPA protocol v2.10


simply-network-serial
---------------------

SDK framework support for Serial interface connection to IQRF DPA network


simply-network-spi
------------------

SDK framework support for SPI interface connection to IQRF DPA network


simply-network-udp
------------------

SDK framework support for UDP interface connection to IQRF DPA network


simply-network-usbcdc
---------------------

SDK framework support for CDC interface connection to IQRF DPA network

