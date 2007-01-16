/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2006 University of Lisbon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Alexandre Pinto and Hugo Miranda.
 * Contributor(s): See Appia web page for a list of contributors.
 */
package org.continuent.appia.protocols.fifo;

//////////////////////////////////////////////////////////////////////
////
//Appia: protocol development and composition framework            //
////
//Version: 1.0/J                                                   //
////
//Copyright, 2000, Universidade de Lisboa                          //
//All rights reserved                                              //
//See license.txt for further information                          //
////
//Class: FifoLayer                                                 //
////
//Author: Nuno Carvalho, 11/2001                                   //
////
//Change Log:                                                      //
//////////////////////////////////////////////////////////////////////

import org.continuent.appia.core.*;
import org.continuent.appia.protocols.common.FIFOUndeliveredEvent;
import org.continuent.appia.protocols.common.SendableNotDeliveredEvent;


/**
 * FifoLayer is the layer for the protocol providing reliable ordered 
 * delivery of messages.
 *
 * The protocol provides the following events:
 * <ul>
 * <li>FifoTimer: Periodic timer used for message resending
 * <li>AckEvent: Sendable event used for positive cumulative confirmation 
 * of messages
 * <li>FIFOUndeliveredEvent: Event used to inform upper layers that a 
 * message could not be delivered.
 * </ul>
 *
 * The protocol accepts/requires the following events:
 * <ul>
 * <li>SendableEvent (Require): Events ordered by the protocol.
 * <li>ChannelInit (Require)
 * <li>ChannelClose (Require)
 * <li>FifoTimer (Require)
 * <li>MaxPDUSizeEvent (Accept): Decrements the pdu size by eigth bytes
 * <li>RegisterSocketEvent (Require): Used to learn wich port (point to point) 
 * was opened for communication
 * <li>Debug (Accept): The protocol follows the usual procedures for 
 * handling Debug information
 * <li>FIFOConfigEvent (Accept): Event for configuration of several 
 * parameters of the protocol.
 * <li>MulticastInitEvent (Accept): Used to learn wich multicast 
 * address will be used for communication
 * </ul>
 *
 * @see org.continuent.appia.core.Layer
 * @see FifoSession
 * @see FifoTimer
 * @see AckEvent
 * @see FIFOUndeliveredEvent
 * @see org.continuent.appia.core.events.SendableEvent
 * @see org.continuent.appia.core.events.channel.ChannelInit
 * @see org.continuent.appia.core.events.channel.ChannelClose
 * @see org.continuent.appia.protocols.frag.MaxPDUSizeEvent
 * @see org.continuent.appia.core.events.channel.Debug
 * @see FIFOConfigEvent
 * @see org.continuent.appia.protocols.udpsimple.MulticastInitEvent
 *
 * @author Nuno Carvalho
 */
public class FifoLayer 
extends Layer
implements org.continuent.appia.core.events.AppiaMulticastSupport{
    
    public FifoLayer() {
        super();
        
        /* Events enumeration */
        evProvide=new Class[]{
                org.continuent.appia.core.events.SendableEvent.class,
                org.continuent.appia.protocols.fifo.FifoTimer.class,
                org.continuent.appia.protocols.fifo.AckEvent.class,
                org.continuent.appia.protocols.common.FIFOUndeliveredEvent.class,
        };
        
        evRequire=new Class[]{
                org.continuent.appia.core.events.SendableEvent.class,
                org.continuent.appia.core.events.channel.ChannelInit.class,
                org.continuent.appia.core.events.channel.ChannelClose.class,
                org.continuent.appia.protocols.fifo.FifoTimer.class,
                org.continuent.appia.protocols.common.RegisterSocketEvent.class,
        };
        
        evAccept=new Class[]{
                org.continuent.appia.core.events.SendableEvent.class,
                org.continuent.appia.core.events.channel.ChannelInit.class,
                org.continuent.appia.core.events.channel.ChannelClose.class,
                org.continuent.appia.core.events.channel.Debug.class,
                org.continuent.appia.protocols.fifo.FifoTimer.class,
                org.continuent.appia.protocols.fifo.FIFOConfigEvent.class,
                org.continuent.appia.protocols.common.RegisterSocketEvent.class,
                SendableNotDeliveredEvent.class,
                org.continuent.appia.protocols.frag.MaxPDUSizeEvent.class,
        };
    }
    
    /**
     * Standard session instantiation
     */
    public Session createSession() {
        return new FifoSession(this);
    }
}
