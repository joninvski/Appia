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
 
package org.continuent.appia.protocols.group.heal;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.SendableEvent;



/**
 * Event exchanged between HealLayers, to notify the existance of a particular
 * view, and therefore detect concurrent views of the same group.
 * <br>
 * The event is sent by the HealLayer. The GossipOutLayer then changes the
 * event to the "Gossip Channel" to be sent to the Gossip Server. In the Gossip
 * Server it is propagated to other HealLayer.
 * <br>
 * Although a SendableEvent it uses an ExtendedMessage for the payload.
 *
 * @author Alexandre Pinto
 * @version 0.1
 * @see org.continuent.appia.protocols.group.heal.HealLayer
 * @see org.continuent.appia.protocols.group.heal.GossipOutLayer
 * @see org.continuent.appia.gossip.GossipServer
 * @see org.continuent.appia.core.message.Message
 */
public class GossipOutEvent extends SendableEvent {

  /**
   * Creates an uninitialized GossipOutEvent.
   * <br>
   * The payload is an ExtendedMessage.
   *
   * @see org.continuent.appia.core.message.Message
   */
  public GossipOutEvent() {
    super();
  }

  /**
   * Creates an initialized GossipOutEvent.
   * <br>
   * The payload is an ExtendedMessage.
   *
   * @param channel the {@link org.continuent.appia.core.Channel Channel} of the Event
   * @param dir the {@link org.continuent.appia.core.Direction Direction} of the Event
   * @param source the {@link org.continuent.appia.core.Session Session} that is generating the Event
   * @throws AppiaEventException as the result of calling
   * {@link org.continuent.appia.core.Event#Event(Channel,int,Session)
   * Event(Channel,Direction,Session)}
   * @see org.continuent.appia.core.message.Message
   */
  public GossipOutEvent(Channel channel, int dir, Session source) throws AppiaEventException {
    super(channel,dir,source);
  }
  
}