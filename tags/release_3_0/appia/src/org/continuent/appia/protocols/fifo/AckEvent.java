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
//                                                                  //
// Appia: protocol development and composition framework             //
//                                                                  //
// Class: AckEvent: Acknowledgment event                            //
//                                                                  //
// Author: Hugo Miranda, 05/2000                                    //
//                                                                  //
// Change Log:                                                      //
//                                                                  //
//////////////////////////////////////////////////////////////////////

import org.continuent.appia.core.*;
import org.continuent.appia.core.events.*;


/**
 * Class AckEvent extends SendableEvent and is intended to send explicit (not
 * piggybacked) acknowledgments of messages.
 *
 * @author Hugo Miranda
 * @see    FifoSession
 * @see    org.continuent.appia.core.Event
 * @see    org.continuent.appia.core.events.SendableEvent
 */
public class AckEvent extends SendableEvent {

    /** 
     * This constructor creates an acknowledge based on a model
     * event.
     * 
     * dest and source fields are switched, direction is
     * inverted. channel is copied from the model and generator is
     * obtained from the session pointer. The sequence number to be
     * confirmed is sent on the Message. This constructor initializes
     * the event.
     *
     * @param c The event received by FifoSession that triggered the acknowledgment.
     * @param gen The session creating the event.
     * @param dest destination of the ack
     * @param source source of the ack
     * @see org.continuent.appia.core.Session
     * @see FifoSession
     * @see org.continuent.appia.protocols.common.InetWithPort
     */
    public AckEvent(Channel c,Session gen, Object dest, Object source) 
	throws AppiaEventException {
	/* Calls the main event Constructor Class */
	super();
	setChannel(c);
	setDir(Direction.DOWN);
	setSource(gen);
	this.dest = dest;
	this.source = source;
	init();
    }

    /**
     * The empty constructor. Required for dynamic creation of instances.
     */
    public AckEvent() {}
}
