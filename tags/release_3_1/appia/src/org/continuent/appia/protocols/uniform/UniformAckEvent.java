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
package org.continuent.appia.protocols.uniform;

import org.continuent.appia.core.*;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.group.*;
import org.continuent.appia.protocols.group.events.*;


public class UniformAckEvent extends GroupSendableEvent{
	
	public UniformAckEvent(){
		super();
	}
	
	/**
	 * 
	 * @param channel event channel
	 * @param dir direction of the event
	 * @param source source session
	 * @param group group where to send the event
	 * @param view_id current view of the group
	 * @throws AppiaEventException
	 */
	public UniformAckEvent(Channel channel, int dir, Session source, Group group, ViewID view_id)
	throws AppiaEventException{
		super(channel,dir,source,group,view_id);
	}
	
	/**
	 * 
	 * @param channel event channel
	 * @param dir direction of the event
	 * @param source source session
	 * @param group group where to send the event
	 * @param view_id current view of the group
	 * @param om message
	 * @throws AppiaEventException
	 */
	public UniformAckEvent(Channel channel, int dir, Session source, Group group, ViewID view_id, Message om) throws AppiaEventException{
		super(channel,dir,source,group,view_id,om);
	}
}
