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
 * Interface for receiving asynchronous messages from connected USB device. When 
 * asynchronous message comes, <code>onGetMessage</code> will be called. 
 * Data of the message will be passed in <code>data</code> parameter.
 * 
 * @version     1.0
 */
public interface J_AsyncMsgListener {
    /**
     * Will be called if asynchronous message comes. <br> 
     * If any error in the asynchronous message is discovered, <code>data</code> 
     * will be set to <code>null</code>.
     * @param data data of asynchronous message.
     */
    public void onGetMessage(short[] data);
    
}
