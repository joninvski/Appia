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
 
/**
 * Title:        Apia<p>
 * Description:  Protocol development and composition framework<p>
 * Copyright:    Copyright (c) Alexandre Pinto & Hugo Miranda & Luis Rodrigues<p>
 * Company:      F.C.U.L.<p>
 * @author Alexandre Pinto & Hugo Miranda & Luis Rodrigues
 * @version 1.0
 */
package org.continuent.appia.protocols.group.stable;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.Debug;
import org.continuent.appia.core.events.channel.PeriodicTimer;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.suspect.Fail;
import org.continuent.appia.protocols.group.suspect.Suspect;


public class StableLayer extends Layer {
  
  public StableLayer() {
    Class gossip=org.continuent.appia.protocols.group.stable.StableGossip.class;
    Class view=View.class;
    Class retransmit=org.continuent.appia.protocols.group.stable.Retransmit.class;
    Class retransmission=org.continuent.appia.protocols.group.stable.Retransmission.class;
    Class periodic=PeriodicTimer.class;
    
    evProvide=new Class[4];
    evProvide[0]=gossip;
    evProvide[1]=retransmit;
    evProvide[2]=retransmission;
    evProvide[3]=Suspect.class;
    
    evRequire=new Class[2];
    evRequire[0]=view;
    evRequire[1]=periodic;
    
    evAccept=new Class[8];
    evAccept[0]=gossip;
    evAccept[1]=view;
    evAccept[2]=retransmit;
    evAccept[3]=retransmission;
    evAccept[4]=Fail.class;
    evAccept[5]=periodic;
    evAccept[6]=GroupSendableEvent.class;
    evAccept[7]=Debug.class;
  }

  public Session createSession() {
    return new StableSession(this);
  }
}