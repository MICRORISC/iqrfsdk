Introduction to LibCDC
----------------------

The LibCDC library intended usage is to provide programming language means for communication 
between PC and IQRF USB device, equipped with the firmware supporting the USB CDC IQRF mode.  

The library is very lightweight and its usage is simple. No handling of some complex settings 
is required. When a user wants to communicate with some USB device, then she simply links the 
library to his program and uses the library's public interface functions.  
  
The library implementation is based on encapsulation of commands, sent from PC to USB device, 
into programming language's functions. More detailed information about that commands are in
document "CDC Implementation in IQRF USB devices, User's Guide" 
(http://www.iqrf.org/weben/downloads.php?id=196).


Features
--------

- intended for communication with IQRF USB devices
- lightweight and easy to use
- decent error handling
- well documented
- supported programming languages: C, C++ and Java
- supported operating systems: Windows, Linux


Functionality
-------------

Before the library can be used, it must be correctly initialized. It can be used default
initialization or it can be specified concrete port.
On Windows system the default communication port is set to COM1.
On Linux system the default communication port is set to  "/dev/ttyACM0".

The library implements particular commands in the form of functions which a user can
call. Because of nature of communication, the library defines inner timeouts
for handling of operations. Values of these timeouts is usually set to 5000 ms. User
defined timeouts settings are not currently supported.
 
Receiving of asynchronous messages(those, which have "DR" prefix) is performed via  
message listener. Message listener is user defined function, which will be called when
asynchronous message comes. Message listener must have specific prototype and user
registers it with the libary via special function. User can have registered at most 
one listener with the library. 

CAUTION:
The library is not thread safe.

 
Error handling
--------------

Errors can occur at various phases in communication. The libary defines several types of 
errors:
1. Send error - arises at phase of sending message to device.
2. Receive error - arises at phase of receiving message from device.

CAUTION:
When some serious error arises during reading data from asociated COM-port, the reading thread 
inside the libary is mandatory stopped and thus it is not possible to read any next data from 
communication port through the library. Because of this, the majority of public interface's functions 
is blocked. For further working must be the library deallocated and initialized again. 

For testing the state of the reading thread some functions are provided. If any function, which is 
dependent to reading thread is called after the reading thread was stopped, this function
simply returns error code or throws exception - according to implementing language.


Supported programming languages
-------------------------------

It is possible to use the libary from within these languages: C, C++, Java. Original implementation 
was written in C++ language. Versions for C a Java are defacto interfaces, which use the original 
C++ implementation inherently (in the form of static and dynamic libraries).
