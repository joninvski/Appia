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
 
package org.continuent.appia.protocols.group.intra;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Session;
import org.continuent.appia.protocols.group.LocalState;
import org.continuent.appia.protocols.group.ViewState;
import org.continuent.appia.protocols.group.events.GroupEvent;


/**
 * {@link org.continuent.appia.protocols.group.events.GroupEvent Event} notifying the
 * installation of a new {@link org.continuent.appia.protocols.group.ViewState view}.
 * <br>
 * The first <i>View</i> marks the begining of <i>Group Communication</i>
 * operation.
 * <br>
 * Except for the first, it allways comes after a
 * {@link org.continuent.appia.protocols.group.sync.BlockOk BlockOk}, and
 * marks the return to normal, unblocked, operation.
 * <br>
 * There are two reasons for a new <i>view</i>:
 * <ul>
 * <li>One, or more, members have failed
 * <li>New members want to join the group
 * </ul>
 *
 * @author Alexandre Pinto
 * @version 0.1
 * @see org.continuent.appia.protocols.group.sync.BlockOk
 * @see org.continuent.appia.protocols.group.ViewState
 * @see org.continuent.appia.protocols.group.LocalState
 */
public class View extends GroupEvent {

  /**
   * The new {@link org.continuent.appia.protocols.group.ViewState ViewState}.
   */
  public ViewState vs;
  /**
   * The new {@link org.continuent.appia.protocols.group.LocalState LocalState}.
   */
  public LocalState ls;

  /**
   * Constructs an uninitialized <i>View</i> event.
   *
   * @param vs the new {@link org.continuent.appia.protocols.group.ViewState ViewState}
   * @param ls the new {@link org.continuent.appia.protocols.group.LocalState LocalState}
   */
  public View(ViewState vs, LocalState ls) {
    super(vs.group,vs.id);
    this.vs=vs;
    this.ls=ls;
  }

  /**
   * Constructs an initialized <i>View</i> Event.
   *
   * @param vs the new {@link org.continuent.appia.protocols.group.ViewState ViewState}
   * @param ls the new {@link org.continuent.appia.protocols.group.LocalState LocalState}
   * @param channel the {@link org.continuent.appia.core.Channel Channel} of the Event
   * @param dir the {@link org.continuent.appia.core.Direction Direction} of the Event
   * @param source the {@link org.continuent.appia.core.Session Session} that is generating the Event
   * @throws AppiaEventException as the result of calling
   * {@link org.continuent.appia.protocols.group.events.GroupEvent#GroupEvent(Channel,int,Session,Group,ViewID)
   * GroupEvent(Channel,int,Session,Group,ViewID)}
   */
  public View(ViewState vs, LocalState ls, Channel channel, int dir, Session source) throws AppiaEventException {
    super(channel,dir,source,vs.group,vs.id);
    this.vs=vs;
    this.ls=ls;
  }
}