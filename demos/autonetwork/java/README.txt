AutoNetwork for Java
====================

Introduction
------------

Algorithm for automatic creation of IQRF DPA networks functionality is based on the idea 
of automatic adding new nodes into IQRF DPA network without any human assistance. All 
phases of the process of addition new node into network is fully automatic, altough 
a human can control this process by setting values for the algorithm's parameters. 

Implementation of the algorithm is based on the jSimply framework. Reader should be 
a little bit familiar with the jSimply framework before experimenting with the Demo. 
Therefore, it is recommended to look through libs-xxx/jSimply-xxx/!ReadMe.txt file 
that explains main concepts of the framework in a form of Get started tutorial.

The algorithm's implementation is intended for IQRF DPA v2.2x.


Parts
-----

It consists of these parts:

- doc

	It contains a diagram with AutoNetwork algorithm for developers and log record 
	from running example on Windows.

- examples

	- bin

		Scripts and jars allowing to run demo from command line without a need to 
		install IDE for Java (Netbeans or Eclipse). However, JRE 1.7 [1] MUST be 
		present.

	- maven

		A demo project for Maven, can be open by e.g. Netbeans IDE.

	- sources

		A demo source code in a form of jar.

- libs

	It contains jSimply and 3rd party libraries which are basis for the demo.

- sources

	It contains source code of the complete AutoNetwork algorithm. An implementation of
	the algorithm can be found in the file AutoNetworkAlgorithmImpl.java. Other files
	define necessary interfaces and helper classes for the used custom peripheral. 

	The source code in this folder has been copied from the artefact simply-iqrf-dpa-v22x.


Running
-------

1)

Before running the demo, make sure that you run DPA v2.2x on your DPA coordinator and DPA nodes. 
Also, make sure that CustomDpaHandler-Coordinator-AutoNetwork-xxx (for the coordinator) and 
CustomDpaHandler-AutoNetwork-xxx (for the nodes) are uploaded to the modules and custom 
handler is enabled in DPA configuration for the modules. Use IQRF IDE for these tasks.    

2)

Supported gateways for the demo are either IQRF USB GW-USB-0x or CK-USB-04A. GW or CK MUST be 
in CDC IQRF mode. Use IQRF IDE for switching GW or CK to this mode. Next, pay attention to 
examples/bin/config/NetworkSettings.xml and set the COM port according to your Windows or 
Linux setup. You can use IQRF IDE and menu tools/Windows Device Manager to find out your
COM port on Windows. Use util dmesg to find out your COM port on Linux.

The demo can be also run on Raspberry PI together with IQRF USB devices or with KON-RASP-01. 
The demo config files are set to IQRF USB CDC-IQRF by default.  

3)

As the AutoNetwork algorithm automatically adds newly bonded nodes and runs discovery, the 
nodes are then accessible from jSimply environment with the peripherals (jSimply calls it 
Device Objects) defined in PeripheralDistribution.xml file.

By default there are standard and one user peripherals enabled and number of nodes is set
to 239. This allows to algorithm to add up to 239 nodes to the network. It can be left as 
it is and peripherals can be set as needed.  

4)

Demo can be run by scripts residing in the examples/bin directory, which are intended for 
supported platforms - according to the corresponding scripts names.

5)

Each script contains default values of parameters of the Algorithm and also default values 
of other parameters, see later. If it is needed to change any of these values, then it is 
neccessarry to change these values inside the script. DO NOT INCLUDE ANY PARAMETERS TO THE
COMMAND LINE ALONG WITH THE SCRIPT NAME! CHANGE THEM INSIDE THE SCRIPT!


All supported parameters and switches:

- help:				prints help message including all parameters and
					switches descriptions, and a usage

Algorithm parameters:

- discoveryTxPower: 		discovery TX power
							allowed range of vales: [0..7]
							default value: 5
							
- prebondingInterval: 		prebonding interval [in seconds]
							allowed range of vales: [15..655]
							default value: 15
							
- authorizeRetries: 		number of retries to authorize prebonded node
							default value: 1
							
- discoveryRetries: 		number of retries to run discovery
							default value: 1
							
- temporaryAddressTimeout: 	timeout to hold temporary address, in [tens of seconds]
							allowed range of vales: [0..65535]
							default value: 60
							
- autoUseFrc:				if to use FRC in checking the accessibility of 
							the newly bonded nodes automatically
							default value: true
							
- nodesNumberToBond: 		number of nodes to bond
							default value: -1 which means: 
							"maximal number of nodes to bond according to the IQRF DPA networks limitations"
							
Other parameters:

- networkId:				ID of the network to run the algorithm on
							default value: 1

- methodIdTransformer:		method ID transformer for P2P Prebonder
							default value: com.microrisc.simply.di_services.MethodIdTransformer.P2PPrebonderStandardTransformer

- maxRunningTime:			maximal runnig time of the algorithm [in seconds]
							default value: -1 which means: "not bounded"

- removeBondsAtend:			broadcast to all nodes to remove their bonds and restart them and clear all 
							bonds on coordinator. It can prepare the network for next test cycle without 
							manual handling.
							default value: true 


ADDITIONAL NOTES (related to jSimply framework and AutoNetwork):

Configuration:

Because of the fact, that the algorithm implementation is based on the jSimply, it is needed to have an 
access to the jSimply's configuration files. These files are stored in the "config" directory.

Allocation and distribution of supported peripherals at each node in connected networks is declared in 
the PeripheralDistribution.xml file. Information from this file is used only if the configuration item 
of 'initialization.type' ( in the Simply.properties file) is set to 'dpa.fixed'. It means, that the 
fixed configuration of distribution of supported peripherals at each node in the connected IQRF 
NETWORK will be used.

Usage of the 'dpa.enumeration' means, that online enumeration process will be used to discover supported 
peripherals at each node in the connected IQRF network. 

In its delivered initial configuration, the Custom peripheral of 0x20 is mapped to the 
com.microrisc.simply.iqrf.dpa.v22x.autonetwork.P2PPrebonder Device Interface. 

This mapping is done by classes in the com.microrisc.simply.iqrf.dpa.v22x.autonetwork.demo.def
package and used in the Simply.properties file. If it is needed to change this setting for some 
reason, it is neccessary to create mapping classes like the ones in the 
com.microrisc.simply.iqrf.dpa.v22x.autonetwork.demo.def package, and set the
configuration items of:

 - dpa.perToDevIfaceMapper.factory.class 
		for mapping between peripherals and Device Interfaces
 - protocolLayer.protocolMapping.factory.class
		for mapping between method calls and packets of used application protocol
		
com.microrisc.simply.iqrf.dpa.v22x.autonetwork.P2PPrebonder is mapped to the 
com.microrisc.simply.iqrf.dpa.v22x.autonetwork.SimpleP2PPrebonder implementation
class, see the ImplMapping.xml file. It is also possible to change this setting 
according to the user needs.

Dependencies:

Demo is dependent on own Microrisc's libraries and on third parties libraries. Because of
this there exist 2 directories under the "libs" directory:

- 3rd
	Intended as a storage of all the libraries comming from third parties
	There is a script that helps to download all the dependencies.

- microrisc
	Contains MICRORISC libraries


References
----------

[1] http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html
