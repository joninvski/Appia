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
import org.continuent.appia.protocols.group.Group;
import org.continuent.appia.protocols.group.ViewID;
import org.continuent.appia.protocols.group.ViewState;
import org.continuent.appia.protocols.group.events.GroupEvent;



/**
 * 
 * This class defines a PreView
 * 
 * @author Alexandre Pinto
 * @version 1.0
 */
public class PreView extends GroupEvent {

    /**
     * The view state of this pre-view
     */
  public ViewState vs;

  /**
   * Creates a new PreView event.
   * @param vs the view state
   * @param channel the channel
   * @param dir direction
   * @param source source session
   * @param group the group
   * @param view_id the view id
   * @throws AppiaEventException
   */
  public PreView(ViewState vs, Channel channel, int dir, Session source, Group group, ViewID view_id) throws AppiaEventException {
    super(channel,dir,source,group,view_id);
    this.vs=vs;
  }

  /**
   * Creates a new PreView event.
   * @param vs the view state
   * @param group the group
   * @param view_id the view id
   */
  public PreView(ViewState vs, Group group, ViewID view_id) {
    super(group,view_id);
    this.vs=vs;
  }
}