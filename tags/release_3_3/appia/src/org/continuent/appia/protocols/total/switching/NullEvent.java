/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2006-2007 University of Lisbon
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
package org.continuent.appia.protocols.total.switching;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.group.Group;
import org.continuent.appia.protocols.group.ViewID;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;

/**
 * This class defines the NullEvent as specified in the Total Order
 * Switching algorithm.
 * 
 * @author Jose Mocito
 * @version 0.7
 */
public class NullEvent extends GroupSendableEvent {

	public NullEvent(Channel channel, int dir, Session source, Group group,
			ViewID view_id) throws AppiaEventException {
		super(channel, dir, source, group, view_id);
	}

	public NullEvent() {
		super();
	}

	public NullEvent(Channel channel, int dir, Session source, Group group,
			ViewID view_id, Message omsg) throws AppiaEventException {
		super(channel, dir, source, group, view_id, omsg);
	}

	public NullEvent(Message omsg) {
		super(omsg);
	}

}
