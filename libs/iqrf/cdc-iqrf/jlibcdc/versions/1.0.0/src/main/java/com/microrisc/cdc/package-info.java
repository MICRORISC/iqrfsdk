/*
 * Public CDC Library
 * Copyright (C) 2012 MICRORISC s.r.o., www.microrisc.com
 * IQRF platform details: www.iqrf.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

/**
 * Provides classes necessary for PC to communicate with connected USB device
 * like through a standard COM port. Intended for use in Windows and Linux OS. 
 * <p>
 * It is intended for devices equipped with the firmware supporting the USB CDC 
 * class. Introduction to problematics can be found in:<br> 
 *      "CDC Implementation in IQRF USB devices"
 * <p>
 * All the functionality this package provides is dependent on C++ implementation
 * of communication between PC and USB device using USB CDC class. This C++ 
 * implementation is provided in the form of dynamic library:<br>
 * - On Windows: CDCLib_JavaStub.dll <br>
 * - On Linux: libCDCLib_JavaStub.so <br>
 * This package functionality is fundamentally Java-WRAPPER around the original
 * C++ implementation, which can be used in its own.  
 */
package com.microrisc.cdc;