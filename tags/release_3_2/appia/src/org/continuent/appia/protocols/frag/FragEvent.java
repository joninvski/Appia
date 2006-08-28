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
 package org.continuent.appia.protocols.frag;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.SendableEvent;


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


/**
 * This event contains all the message fragments
 * except the last one, sent with the original type.
 *
 * @author Hugo Miranda
 * @see FragSession
 * @see SendableEvent
 * @see org.continuent.appia.core.Event
 */
public class FragEvent extends SendableEvent implements Cloneable {
    /**
     * Do-it-all constructor. No need to explicitly perform
     * event initialization.
     * 
     * @param e A template event (possibly the event being fragmented)
     * used to extract the Channel, Direction, Source and Dest attributes.
     * @param gen The session creating the event
     * @see org.continuent.appia.core.Channel
     * @see org.continuent.appia.core.Session
     * @see org.continuent.appia.core.events.SendableEvent
     */
    public FragEvent(SendableEvent e,Session gen) throws AppiaEventException {
	/* Calls the main event Constructor Class */
	super();
	setChannel(e.getChannel());
	setDir(e.getDir());
	setSource(gen);
	this.source=e.source;
	this.dest=e.dest;
	init();
    }
 
    /**
      * Empty constructor. Required for event
      * dynamic instantiation.
      */ 
    public FragEvent() {}
}
