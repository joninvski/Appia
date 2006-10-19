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
 package org.continuent.appia.protocols.udpsimple;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.AppiaMulticastSupport;
import org.continuent.appia.core.events.SendableEvent;
import org.continuent.appia.core.events.channel.ChannelClose;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.core.events.channel.Debug;
import org.continuent.appia.protocols.common.RegisterSocketEvent;
import org.continuent.appia.protocols.frag.MaxPDUSizeEvent;


//////////////////////////////////////////////////////////////////////
//                                                                  //
// Appia: protocol development and composition framework            //
//                                                                  //
// Version: 1.0/J                                                   //
//                                                                  //
// Copyright, 2000, Universidade de Lisboa                          //
// All rights reserved                                              //
// See license.txt for further information                          //
//                                                                  //
// Class: UdpSimpleLayer:   Unreliable send/receive using UDP/IP,   //
//                          allowing point-to-point or multicast    // 
//                          communication                           // 
//                                                                  //
// Author: Hugo Miranda, 05/2000                                    //
//         M.Joao Monteiro, 12/2001                                 //
//                                                                  //
//                                                                  // 
//////////////////////////////////////////////////////////////////////


/**
 * Class UdpSimpleLayer is the Layer subclassing for UdpSimple protocol. This
 * protocol serializes and deserializes SendableEvents to/from the network using
 * UDP point-to-point or multicast sockets.
 *
 * The UdpSimple protocol provides the following events:
 * <ul>
 * <li>SendableEvent: or subclasses of it, depending on the messages received from
 * the network.
 *
 * <li>SendableNotDeliveredEvent: to notify upper protocols that the message could not
 * be delivered. The absense of such an event does not prove the contrary. That is,
 * this protocol doesn't provide reliable delivery.
 *
 * <li>UdpAsyncEvent: do not use. Used for inter-thread communication inside the protocol instance.
 * </ul>
 * The protocol accepts the following events:
 * <ul>
 * <li>RegisterSocketEvent (Require): This event instructs the protocol to bind to a specific
 * UDP port.
 *
 * <li>SendableEvent (Accept): sends SendableEvents to the network using its UDP socket
 *
 * <li>ChannelInit (Accept): Initialization procedures
 *
 * <li>UdpAsyncEvent (Require): used by the protocol reader thread to notify the major session that
 * a new event was received in the socket.
 *
 * <li>Debug(Accept): Dumping status information as defined in the Appia specification.
 *
 * <li>ChannelClose (Accept): closing procedures.
 *
 * <li>MaxPDUSizeEvent (Accept): if requested, replies with the maximum datagram size for IP.
 *
 * <li>MulticastInitEvent (Accept) : This event instructs the protocol to open a socket multicast.
 *
 *</ul>
 * @see Layer
 * @see UdpSimpleSession
 * @see SendableEvent
 * @see SendableNotDeliveredEvent
 * @see RegisterSocketEvent
 * @see MulticastInitEvent
 * @see org.continuent.appia.core.events.channel.ChannelInit
 * @see org.continuent.appia.core.events.channel.Debug
 * @see org.continuent.appia.core.events.channel.ChannelClose
 * @see org.continuent.appia.protocols.frag.MaxPDUSizeEvent
 * @author Hugo Miranda, M.Joao Monteiro
 */

public class UdpSimpleLayer extends Layer implements AppiaMulticastSupport {

	/**
	 * Standard empty constructor
	 */
	public UdpSimpleLayer() {
		super();

		evProvide = new Class[2];
		evProvide[0] = SendableEvent.class;
		evProvide[1] = SendableNotDeliveredEvent.class;

		evRequire = new Class[0];

		evAccept = new Class[7];
		evAccept[0] = SendableEvent.class;
		evAccept[1] = ChannelInit.class;
		evAccept[2] = RegisterSocketEvent.class;
		evAccept[3] = Debug.class;
		evAccept[4] = ChannelClose.class;
		evAccept[5] = MaxPDUSizeEvent.class;
		evAccept[6] = MulticastInitEvent.class;
	}

	public Session createSession() {
		return new UdpSimpleSession(this);
	}
}
