======================================
jSimply - Simple Application Framework
======================================

jSimply is a generic tool – a framework allowing to create applications which need to communicate with 
sensor networks (e.g. IQMESH). In general, jSimply is not connected with any particular technology, but 
forms a ground to support multiple technologies. Technology IQRF DPA is fully implemented and integral 
part of the framework.

Before proceeding, please, get yourself familiar with the overview of the framework capured as single 
mind map, check out these files doc/en/jSimply-Framework.xmind or doc/en/jSimply-Framework.png. As you 
proceed reading you can refer to mind map and get yourself used to the main concepts. We also advise
you to spend a time with DCTR, DPA and IQRF IDE before coming to use jSimply. jSimply requires that
your network is already setup and functioning well. All the setup tasks can be nicely handled by 
IQRF IDE.


====================
Get Started Tutorial
====================

Described parts (read carefully, clarify with us main concepts as required)
---------------------------------------------------------------------------

1. 	Introduction
2. 	Configuration
3. 	Simply object
4. 	Network objects
5. 	Node objects
6. 	Device objects
7. 	Call Result objects
8.  Timing machine
9.  Processing and error states
10. FRC Device object
11. Broadcast objects
12. Asynchronous messages
13. Conclusion


1. Introduction
===============

Fundamental concept of jSimply is based on an idea of encapsulating of each device or DPA peripheral 
(sensors, actuators, DPA IO, DPA FRC) on the node in the sensor network into Java-interface. This 
Java-interface is then used to interact with a corresponding device or DPA peripheral in the 
network.

Example:

Temperature sensor has its own interface called Thermometer which defines method getTemperature returning 
current temperature acquired by the sensor.

public interface Thermometer {
	int getTemperature();
}

These interfaces are called Device Interfaces (DI) in the environment of jSimply. Objects that implements 
DI represents particular devices in the network and are called Device Objects (DO). Specific implementation 
of individual DI is in resposibility of a provider of these implementations, jSimply Core does not provide 
any default implementations. Simply-IQRF-DPA-v2xx provides full implementation of standard peripherals in
a form of Device Objects (DO).


Networks, Nodes, Devices
------------------------

From the perspective of an user, jSimply framework is a collection of connected networks (Networks). Each 
connected network is a collection of nodes (Nodes) and each node is a collection of devices (Devices) that 
form its functionality. This functionality is accessible via DI. Each network has its own unique ID within 
jSimply framework. Similarly, each node has its own ID within its network. There is at most one DO on the 
node which links to one concrete device present or connected to this node.


Main entities/layers
--------------------

Main entities which part jSimply into neighbouring layers are:

- User applications (End application with an option to define user's peripherals)
- Device objects 	  (Standard DPA peripherals)
- Connector      	  (ResponseWaiting)
- Protocol layer 	  (DPA v2.10 and v2.20)
- Network layer  	  (CDC-IQRF, CDC-UART, RPI-SPI, UDP)

Each of this layer offers services to the directly next higher layer (Network Layer is the lowest 
layer). These services are provided in a form of service interface which is specific for each 
layer.


Device Objects
--------------

Device Object DO represents concrete device (sensor, actuator, uC periphery) on concrete node in 
concrete connected network. Basic functionality of the DO defines DeviceObject interface. It 
contains methods to access:

- Network ID which the node belongs to
- Node ID which contains this device 
- Class object which specifies implemented Device Interface

Base implementation of this interface is BaseDeviceObject class that serves as a ground for next 
possible extensions.


Connector
---------

Creates link between clients which send Call Requests, usually Device Objects, with the rest of the 
framework. Connector is a mediator of processing of Call Requests.

Main tasks handled:

- management of outgoing messages (Call Requests) and sending them to the connected networks by 
  means of lower layers

- management of incoming Messages (responses) and directing their data (Call results) back to 
  the clients which issued corresponding Call Requests 

The connector can be relatively complex entity – depending on the implemented management. jSimply 
defines so called Response Waiting Connector and provides also its simple implementation.

Response Waiting Connector is a connector that sends Call Request and waits for the Response of this 
Call Request. A duration of this waiting is under a control of the connector and it can provide 
interface for its setting. Important is that this connector DOES NOT send next Call Request 
before:

1. There is a Response for a previous Call Request
2. Or timeout expires


Protocol layer
--------------

Is a layer which provides a communication between Connector and Network Layer by means of specified 
application protocol.

Main functionality:

- Conversion of incoming Call Requests to the data of application protocol and forwarding them to the 
  Network Layer

- Conversion of incoming data from the Network Layer into Messages ( objects of type of AbstractMessage ) 
  and forwarding them to the registered Protocol Listener which is, in typical case, a Connector coupling 
  of incoming Responses with sent Call Requests


Network layer
-------------

Encapsulates an access to the connected network and its communication technology.

Main functionality:

- Getting application protocol data from the Protocol Layer, its conversion to used network communication 
  technology and sending the converted data to the network

- Reading data from the connected network, extraction of useful data from it and forwarding that extracted 
  data to the registered listener (what is typically a Protocol Layer)

jSimply currently defines this types of information about network connections:

COM: com.microrisc.simply.network.comport
SPI: com.microrisc.simply.network.spi
UDP: com.microrisc.simply.network.udp


Asynchronous method calls from DI
---------------------------------

The principle of asynchronous calls is a separation of method calls from DI into 2 phases: 
sending Call Request and waiting for its result. 

For sending a Call Request, the user calls requested method from DI and, as a return value, gets identifier 
of this Call Request in the form of object java.util.UUID.  While the Call request is processed  and user's 
thread is not blocked. When the user requires to receive result of some Call Request, he can use one of the 
getCallResult method. 

Furthermore, the user has an option to specify a default timeout.


Synchronous method calls from DI
--------------------------------

Synchronous calls are usual blocking call of methods. The user simply calls method of required functionality 
from Device Interface and waits for a result. The user thread is blocked for all the time while waiting for 
the result. 

This approach has a drawback. The user must wait till there is call result which in some cases, especially 
in lager networks, can last number of tens of seconds or even minutes. There is a huge difference between 
calling of local and non-local functionaity.


2. Configuration
================

jSimply does not impose any particular configuration management. However, jSimply implements direct 
support for certain default configuration management.

Initial configuration setting is available in a so called main configuration file located in config
folder of the application:

- Simply.properties

Initial configuration file does not contain all settings directly but via references to additional 
configuration files also located in config folder of application. These are:

- ImplMapping.xml
- NetworkConnectionTypes.xml
- NetworkSettings.xml
- PeripheralDistribution.xml


ImplMapping.xml
---------------

The file links chosen Device Interfaces ( written as Java interface ) to implementation classes. 
The classes are used to create relevant Device Objects for each node in the connected networks.
THE USER MUST MODIFY THE FILE ONLY IF NEW E.G. USER'S DPA PERIPHERAL IS INTRODUCED. 


NetworkConnectionTypes.xml
--------------------------

The file specifies network connection type for the networks that shall jSimply communicate with. 
THE USER MUST SELECT TYPE OF NETWORK CONNECTION. BY DEFAULT IS SERIAL CDC-IQRF. 


NetworkSettings.xml
-------------------

The file specifies settings of the networks to connect with jSimply. THE USER MUST SELECT 
HER/HIS PORT IN THIS FILE FOR DEFAULT SERIAL CDC-IQRF TYPE.


PeripheralDistribution.xml
--------------------------

The file defines which DPA peripherals are selected on the nodes in the network. THE USER
MUST DEFINE WHICH PERIPHERALS ARE ENABLED ON THE NODES. THIS MUST MATCH TO HOW THE USER
CONFIGURED DCTR PERIPHERAL BEFOREHAND BY USING IQRF IDE. 


Summary
-------

The default setting can be used. By default:

- Network layer: 	  CDC-IQRF (CK-USB-04A or GW-USB-0x)
- Protocol layer:   DPA (v2.1x or v2.2x)
- Connector:        ResponseWaiting
- Device Objects:	  According to the settings in PeripheralDistribution.xml


3. Simply object
================

There is shown how the main object DPA_Simply is acquired. This is the first object the user 
acquires to access other objects abstracting complete IQRF DPA network.

DPA_Simply simply = DPA_SimplyFactory.getSimply("config" + File.separator + "Simply.properties");

All the objects are created for the user according to her/his configuration which has been 
layouted out in previous chapter. The DOs are created according to inicialization type option 
= dpa.fixed. This means that all DOs related to DPA peripherals defined in 
PeripheralDistribution.xml are created and ready for the user.

In case of inicialization type option = dpa.enumeration jSimply firstly ask each node in the
network by means of peripheral enumeration packet for the enabled peripherals on the node
and then accordingly creates relevant DOs for the user.

It is fully up to user which way she selects. BY DEFAULT initialization.type = dpa.fixed
IS USED.


4. Network objects
==================

Network network = simply.getNetwork(networkId, Network.class);
...

The user gets reference to connected network holding objects of all the nodes in the network.


5. Node objects
===============

In order to communicate with a certain node in the network the user acquires its reference
by following code construction:

Node coordinator = network.getNode("0");
Node node1 = network.getNode("1");
...

It is also possible to get reference to all nodes via a map interface:
Map<String, Node> nodeMap = network.getNodesMap();

And it is possible to get a number of bonded nodes in the network:
int numberOfBondedNodes = network.getNodesMap().size() - 1;


6. Device objects
=================

In order to access any DPA peripheral the user acquires relevant Device Object DO on the
selected node in the network:

//FRC frc = coordinator.getDeviceObject(FRC.class);
Thermometer thermometer = node1.getDeviceObject(Thermometer.class); 
...

It is also possible to have a loop and create a map including DO of every or some nodes 
in the network:

Map<String, Thermometer> thermoMap = new LinkedHashMap<>();

for( Integer nodeId : nodeIds ) {
    Node node = network.getNode(nodeId.toString());
    Thermometer thermometer = node.getDeviceObject(Thermometer.class);
    thermoMap.put(nodeId.toString(), thermometer);
}


7. Call Result objects
======================

Once the DO is acqiuired, there are two options how to call any functionality ( DPA peripheral 
command ) on the node in the network. Either invoke async method or sync method of the 
selected DO.


Async method of the DO Thermometer
----------------------------------

Defines splitting of method call into two phases:

1. Sending request and returning ID of this request (Call Request)
2. Blocking waiting for the result (according to ID of the sent request) with an option to define timeout 

The user is not blocked by an issued request. During the time the request is being processed, the user can 
handle other tasks, e.g. to send other call requests. Once there is a need, the user can issue blocking 
call to ask for the result with an option for timeout and repeated waiting.

Thermometer_values thermoValues = null;
UUID tempRequestUid = thermometer.async_get();

Thread.sleep(5000);

//thermoValues = thermo.getCallResultInDefaultWaitingTimeout(tempRequestUid, Thermometer_values.class);
thermoValues = thermo.getCallResultImmediately(tempRequestUid, Thermometer_values.class);

System.out.println( "Temperature: \n" + thermoValues.toPrettyFormattedString() );

The timing will be discuss in a separate paragraph.


Sync method of the DO Thermometer
---------------------------------

Synchronous access stands for an usual blocking call of methods. The user simply calls method of required 
functionality from Device Interface and waits for a result. The user thread is blocked for all the time 
while waiting for the result.

This approach has a drawback. The user must wait till there is call result which in some cases, especially 
in lager networks, can last number of tens of seconds or even minutes. There is a huge difference between 
calling of local and non-local functionaity.

Thermometer_values thermoValues = thermometer.get();
System.out.println("Temperature: \n" + thermoValues.toPrettyFormattedString());

It is possible to read the temperature by acquiring DO from the map created in paragraph 6.  

for ( String nodeId : thermoMap.keySet() ) {
    Thermometer thermo = thermoMap.get(nodeId);
    Thermometer_values thermoValues = thermo.get(); 
    System.out.println( "Temperature: \n" + thermoValues.toPrettyFormattedString() );
}

The timing will be discuss in a separate paragraph.


8. Timing machine
=================

Default timeout for most of the DO method calls is set to UNLIMITED by default. In such a case the 
timing is handled by a timing state machine operating on the protocol layer. The protocol layer has
all required informations (in DPA confirmations) in order to calculate right timeout for each method
call according to DPA timing specification. The correct timing depends on parameters such as RF MODE 
(STD, LP) and type of TR module (TR-72, 52). These parametrs are handled by jSimply timing machine 
automatically and there is no need to deal with them.

There are couple of exceptions to default calls such as a process of bonding, discovery and FRC calls. 
Such calls are NOT handled by designed timing machine according to DPA timing specification but are 
left to the user to define timeouts wisely according to the user network.

Forthermore, it is possible to override default timeout by the method below. In this case the user 
MUST choose timeout wisely in order to receive DPA response in a form of Call Result. Setting wrong
timeouts and sending more DPA requests (DO method calls) successively might lead to RF collisions
and resulting in network failures.


Async method calls
------------------

The user can set specific timeout by:

thermo.setDefaultWaitingTimeout(5000);
Thermometer_values thermoValues = null;
UUID tempRequestUid = thermometer.async_get();
...
thermoValues = thermo.getCallResultInDefaultWaitingTimeout(tempRequestUid, Thermometer_values.class);

The method blocks for upto 5s in order to receive DPA response from the network in a form of Call 
result object.


Sync method calls
-----------------

The user can set specific timeout by:

thermo.setDefaultWaitingTimeout(5000);
Thermometer_values thermoValues = thermometer.get();
System.out.println("Temperature: \n" + thermoValues.toPrettyFormattedString());

If the use set the timeout back to UNLIMITED then again the timing is handled by timing state 
machine.

thermo.setDefaultWaitingTimeout( WaitingTimeoutService.UNLIMITED_WAITING_TIMEOUT );

Then DO method blocks for upto a time which is calculated dynamically for each call by timing 
state machine. Please, check DPA timing specification for further details.


9. Processing and error states
==============================

Thermometer_values thermoValues = thermometer.get();

if (thermoValues == null) {
  CallRequestProcessingState procState = thermo.getCallRequestProcessingStateOfLastCall();

/*
  Processing has following states:

  - WAITING_FOR_PROCESSING
  - WAITING_FOR_RESULT
  - RESULT_ARRIVED
  - CANCELLED
  - ERROR
*/

  if (procState == CallRequestProcessingState.ERROR) {
    CallRequestProcessingError error = coordinator.getCallRequestProcessingErrorOfLastCall();

/*  
  Types of errors which encounter during processing of a call requests.

  -DISPATCHING_REQUEST_TO_CONNECTOR
  -DISPATCHING_REQUEST_TO_PROTOCOL_LAYER    
  -PROCESSING_REQUEST_AT_PROTOCOL_LAYER
  -PROCESSING_RESPONSE_AT_PROTOCOL_LAYER
  -NETWORK_INTERNAL
*/

  }
}


10. FRC Device object
=====================

It is shown here how to access FRC peripheral using jSimply framework. 

Getting reference to the coordinator and its FRC peripheral:

Node coordinator = network.getNode("0");
FRC frc = coordinator.getDeviceObject(FRC.class);


Standard FRC commands
---------------------

FRC_Data frcData = frc.send(FRC_Command frcCmd);
short[] frcExtraData = frc.extraResult();

Available FRC_Command commands:

// UART or SPI data available - 0x01
// Any FRC bits command can use this FRC generic object
FRC_UniversalWithBits frcCmd = new FRC_UniversalWithBits(0x01);

// Temperature - 0x80
// Any FRC byte command can use this FRC generic object
FRC_UniversalWithBytes frcCmd = new FRC_UniversalWithBytes(0x80);

// Other prepared commands

-FRC_Prebonding
-FRC_UART_SPI_data
-FRC_AcknowledgedBroadcastBits
-FRC_Temperature
-FRC_AcknowledgedBroadcastBytes
-FRC_MemoryRead
-FRC_MemoryReadPlus1

FRC_Data frcData = frc.send(frcCmd);
short[] frcExtraData = frc.extraResult();

The result is available as short array or can be further parsed e.g. into map structure.


Selective FRC commands
----------------------

The user can select number of nodes on which the FRC command will be executed. 

Node[] selectedNodes = network.getNodes( new String[]{ "1", "3" } );

FRC_Data frcData = frc.sendSelective( new FRC_Temperature(selectedNodes) );
FRC_Data frcData = frc.sendSelective( new FRC_UniversalWithBytes(0x80, selectedNodes) );
short[] frcExtraData = frc.extraResult();

The result is available as short array or can be further parsed e.g. into map structure.


FRC Response time
-----------------

The user can specify FRC response time which nodes need in order to acquire FRC return value
e.g. reading from slower temperature sensor.

VoidType paramsResult = frc.setFRCParams(FRC_Configuration frcConfig);

FRC_RESPONSE_TIME in FRC configuration defines maximum time reserved for preparing return FRC 
value.

- TIME_40_MS(40)
- TIME_320_MS(320)
- TIME_640_MS(640)
- TIME_1280_MS(1280)
- TIME_2560_MS(2560)
- TIME_5120_MS(5120)
- TIME_10240_MS(10240)
- TIME_20480_MS(20480)


FRC timing
----------

How to calculate and set correct timeout for any FRC command:

1) Typical standard FRC (can transfer up to 2B to the nodes) duration is lower than:

timeout = Bonded Nodes x 130 + _RESPONSE_FRC_TIME_xxx_MS + 250 [ms]

2) Typical advanced FRC (can transfer up to 30B to the nodes) duration is lower than:

STD mode:
timeout = Bonded Nodes x 150 + _RESPONSE_FRC_TIME_xxx_MS + 290 [ms].

LP mode:
timeout = Bonded Nodes x 200 + _RESPONSE_FRC_TIME_xxx_MS + 390 [ms].


Node coordinator = network.getNode("0");
FRC frc = coordinator.getDeviceObject(FRC.class);

frc.setDefaultWaitingTimeout(timeout);

FRC_UniversalWithBytes frcCmd = new FRC_UniversalWithBytes(0x80);
FRC_Data frcData = frc.send(frcCmd);

This method call blocks till the result is available according to the calculated timeout.
The user can use frc.async_send(frcCmd) in order not to block application thread and be
able to perform other tasks while waiting for the FRC response.

frc.setDefaultWaitingTimeout(timeout);

// First up to 57 bytes of FRC result
// short array as a parametr indicates bytes that are being transmitted to the nodes in the network
UUID getFRCRequestUid = frc.async_send( new FRC_Temperature( new short[] { 0, 0, 0, 0, 0 } ));
        
// ... do some other work
System.out.println("Sleeping ... waiting for FRC result");
Thread.sleep(timeout);
        
// getting the result of method call
FRC_Data frcData = frc.getCallResultInDefaultWaitingTimeout(getFRCRequestUid, FRC_Data.class);

// remaining bytes of FRC result
short[] frcExtraData = frc.extraResult();


11. Broadcast objects
=====================

The main idea of broadcasting implementation within jSimply DPA Extension is based on the idea, that a 
broadcast request is simply the special type of an ordinary call request with a node identifier left 
unspecified. A result of the broadcast request can simply be of one of 2 states:

OK: if broadcast has performed correctly 
Error: if an error has occured during broadcast request processing


Async calls
-----------

Getting a reference to broadcast object:
BroadcastServices broadcastServices = simply.getBroadcastServices();

LED_State lStateOn = LED_State.ON;
LED_State lStateOff = LED_State.OFF;

UUID requestId1 = broadcastServices.sendRequest(
                    network1.getId(), LEDR.class, LEDG.MethodID.SET, 
                    new Object[] { lStateOn } );

Thread.sleep(500);

UUID requestId2 = broadcastServices.sendRequest(
                    network1.getId(), LEDR.class, LEDG.MethodID.SET, 
                    new Object[] { lStateOff } );

And getting DPA request status call in max. 1s.

BroadcastResult broadcastResult1 = broadcastServices.getBroadcastResult(requestId1, 1000);
BroadcastResult broadcastResult2 = broadcastServices.getBroadcastResult(requestId2, 1000);

if ( broadcastResult1 == BroadcastResult.OK  &&  broadcastResult2 == BroadcastResult.OK ) {
  // OK
}


Sync calls
----------

Getting a reference to broadcast object:
BroadcastServices broadcastServices = simply.getBroadcastServices();

BroadcastResult nodesDisablingResult = broadcastServices.broadcast(
                                          networkId, Node.class, Node.MethodID.ENABLE_REMOTE_BONDING,
                                          new Object[] { 0, 0, new short[] { 0, 0 } } );

if ( nodesDisablingResult == null ) {
  // ERROR
}


12. Asynchronous messages
=========================

These messages are DPA responses without DPA requests being sent prior to receiving them.   


AsynchronousMessages msgListener = new AsynchronousMessages();
        
Getting access to asynchronous messaging manager:
AsynchronousMessagingManager<DPA_AsynchronousMessage, DPA_AsynchronousMessageProperties> asyncManager 
                = simply.getAsynchronousMessagingManager();
        
Register the listener of asynchronous messages:
asyncManager.registerAsyncMsgListener(msgListener);


An user method that is invoked upon receiving the DPA response:   

public void onAsynchronousMessage(DPA_AsynchronousMessage message) {
  // MESSAGE PROCESSING
}


13. Conclusion
==============

The intention of this document is to introduce main concepts of jSimply framework and show to the 
user basic programming constructions that can be used to access particular functionality in IQRF 
DPA network. Try out the prepared examples practically!

Please, let us know if there are any parts which need further clarifications!
