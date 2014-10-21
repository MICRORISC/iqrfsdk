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

package com.microrisc.cdc;

/**
 * Receive messages exceptions for J_CDCImpl class. This exceptions occur when
 * some error has occured during data reception from USB device.
 * <p>
 * Peer class for CDCReceiveException class.
 * 
 * @version     1.0
 */
public class J_CDCReceiveException extends J_CDCImplException {
    /**
     * Creates exception object.
     * @param msg description of exception
     */
    public J_CDCReceiveException(String msg) {
       super(msg); 
    }
}
