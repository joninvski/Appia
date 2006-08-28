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

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.protocols.group.AppiaGroupError;


/**
 * Layer that gracefully removes the member from the Group.
 * <br>
 * When a member wishes to leave the group, it must send a LeaveEvent
 * downwards. When the protocol finishes a ExitEvent will be received.
 * <!-- Also at the end of the protocol the Channel will be automatically closed. -->
 * <br>
 * If the group is {@link org.continuent.appia.protocols.group.sync.BlockOk blocked} there
 * isn't any guarantee that a leave wil succeed. If after the sending of a
 * LeaveEvent a View event is received instead of the ExitEvent then the
 * leave request should be retransmitted in the new view.
 *
 * @author Alexandre Pinto
 * @version 0.1
 * @see org.continuent.appia.protocols.group.leave.LeaveEvent
 * @see org.continuent.appia.protocols.group.leave.ExitEvent
 * @see org.continuent.appia.core.Channel#end
 * @see org.continuent.appia.protocols.group.intra.View
 * @see org.continuent.appia.protocols.group.sync.BlockOk
 */
public class LeaveLayer extends Layer {

  /**
   * Creates LeaveLayer.
   * <br>
   *
   * <b>Events Provided</b><br>
   * <ul>
   * <li>appia.protocols.group.intra.ViewChange
   * <li>appia.protocols.group.leave.ExitEvent
   * </ul>
   *
   * <b>Events Required</b><br>
   * <ul>
   * <li>appia.protocols.group.intra.View
   * <li>appia.protocols.group.intra.PreView
   * </ul>
   *
   * <b>Events Accepted</b>
   * <ul>
   * <li>appia.protocols.group.intra.View
   * <li>appia.protocols.group.intra.PreView
   * <li>appia.protocols.group.leave.LeaveEvent
   * <li>appia.protocols.group.leave.ExitEvent
   * <li>appia.events.channel.Debug
   * </ul>
   */
  public LeaveLayer() {
	  
      Class view=org.continuent.appia.protocols.group.intra.View.class;
      Class debug=org.continuent.appia.core.events.channel.Debug.class;
      Class change=org.continuent.appia.protocols.group.intra.ViewChange.class;
      Class preview=org.continuent.appia.protocols.group.intra.PreView.class;
      Class leave=org.continuent.appia.protocols.group.leave.LeaveEvent.class;
      Class exit=org.continuent.appia.protocols.group.leave.ExitEvent.class;

      evProvide=new Class[2];
      evProvide[0]=change;
      evProvide[1]=exit;

      evRequire=new Class[2];
      evRequire[0]=view;
      evRequire[1]=preview;

      evAccept=new Class[5];
      evAccept[0]=view;
      evAccept[1]=preview;
      evAccept[2]=leave;
      evAccept[3]=exit;
      evAccept[4]=debug;

  }

  /**
   * Creates a new LeaveSession.
   */
  public Session createSession() {
    return new LeaveSession(this);
  }
}