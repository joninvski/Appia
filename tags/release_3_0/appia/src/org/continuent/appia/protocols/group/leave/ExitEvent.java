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
 package org.continuent.appia.protocols.group.leave;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.SendableEvent;
import org.continuent.appia.protocols.group.Group;
import org.continuent.appia.protocols.group.ViewID;



/**
 * Event received when the member has left the group.
 * <br>
 * See {@link org.continuent.appia.protocols.group.leave.LeaveLayer LeaveLayer} for more
 * details.
 *
 * @author Alexandre Pinto
 * @version 0.1
 * @see org.continuent.appia.protocols.group.leave.LeaveLayer
 * @see org.continuent.appia.protocols.group.ViewState
 */
public class ExitEvent extends SendableEvent {

  /**
   * The group left.
   */
  public Group group;

  /**
   * The view id which it left.
   */
  public ViewID view_id;

  /**
   * Constructs an initialized <i>ExitEvent</i>.
   *
   * @param channel the {@link org.continuent.appia.core.Channel Channel} of the Event
   * @param dir the {@link org.continuent.appia.core.Direction Direction} of the Event
   * @param source the {@link org.continuent.appia.core.Session Session} that is generating the Event
   * @throws AppiaEventException as the result of calling
   * {@link org.continuent.appia.core.Event#Event(Channel,int,Session)
   * Event(Channel,int,Session)}
   */
  public ExitEvent(Channel channel, int dir, Session source) throws AppiaEventException {
    super(channel,dir,source);
  }

  /**
   * Constructs an uninitialized <i>ExitEvent</i>.
   */
  public ExitEvent() {
    super();
  }

}