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
 package org.continuent.appia.protocols.group.bottom;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.Debug;
import org.continuent.appia.protocols.group.events.GroupEvent;
import org.continuent.appia.protocols.group.events.GroupInit;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;



/**
 * The <i>group communication</i> bottom layer.
 * <br>
 * As the name implies, this should be the lowest of the group coomunication
 * layers in the protocols stack.
 *
 * @version 0.1
 * @author Alexandre Pinto
 */
public class GroupBottomLayer extends Layer {
  
  /**
   * Creates GroupBottomLayer.
   * <br>
   *
   * <b>Events Provided</b><br>
   * <i>none</i>
   *
   * <b>Events Required</b><br>
   * <ul>
   * <li>appia.protocols.group.intra.View
   * </ul>
   *
   * <b>Events Accepted</b>
   * <ul>
   * <li>appia.protocols.group.events.GroupSendableEvent
   * <li>appia.protocols.group.intra.View
   * <li>appia.protocols.group.events.GroupInit
   * <li>appia.events.channel.Debug
   * <li>appia.protocols.group.events.GroupEvent
   * </ul>
   */
  public GroupBottomLayer() {
    Class view=View.class;
    Class other=org.continuent.appia.protocols.group.bottom.OtherViews.class;
    
    evProvide=new Class[1];
    evProvide[0]=other;
    
    evRequire=new Class[1];
    evRequire[0]=view;
    
    evAccept=new Class[5];
    evAccept[0]=GroupSendableEvent.class;
    evAccept[1]=view;
    evAccept[2]=GroupInit.class;
    evAccept[3]=Debug.class;
    evAccept[4]=GroupEvent.class;
  }
  
  /**
   * Creates a new GroupBottomSession.
   */
  public Session createSession() {
    return new GroupBottomSession(this);
  }
}