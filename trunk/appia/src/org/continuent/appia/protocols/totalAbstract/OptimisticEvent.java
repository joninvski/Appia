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
 /*
 * Created on 12-Apr-2005
 *
 */
package org.continuent.appia.protocols.totalAbstract;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Session;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;


/**
 * Event used by total order protocols to deliver messages with optimistic assumptions
 * The original message is encapsulated inside this event.
 * @author nunomrc
 *
 */
public class OptimisticEvent extends Event {
	
	// the event to deliver
	private GroupSendableEvent event;

	/**
	 * Basic constructor.
	 */
	public OptimisticEvent() {
		super();
	}

	/**
	 * Builds a new OptimisticEvent with the message to deliver
	 * @param e the message to deliver.
	 */
	public OptimisticEvent(GroupSendableEvent e) {
		super();
		event = e;
	}

	/**
	 * @param channel the message channel
	 * @param dir direction of the event
	 * @param src source session
	 * @param e the event to deliver
	 * @throws AppiaEventException
	 */
	public OptimisticEvent(Channel channel, int dir, Session src, GroupSendableEvent e)
			throws AppiaEventException {
		super(channel, dir, src);
		event = e;
	}

	/**
	 * Gets the stored event to deliver.
	 * @return Returns the event.
	 */
	public GroupSendableEvent getEvent() {
		return event;
	}
	/**
	 * Sets the event to deliver.
	 * @param event The event to set.
	 */
	public void setEvent(GroupSendableEvent event) {
		this.event = event;
	}
}
