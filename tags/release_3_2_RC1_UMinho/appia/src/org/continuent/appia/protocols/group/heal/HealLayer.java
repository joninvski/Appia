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
package org.continuent.appia.protocols.group.heal;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.Debug;
import org.continuent.appia.core.events.channel.PeriodicTimer;
import org.continuent.appia.protocols.group.bottom.OtherViews;
import org.continuent.appia.protocols.group.events.GroupInit;
import org.continuent.appia.protocols.group.intra.View;


public class HealLayer extends Layer {
  
  public static final long GOSSIP_TIME=10000; // 10 secs
  public static final long HELLO_MIN_TIME=2500; // 2.5 secs 
  
  public HealLayer() {
    Class view=View.class;
    Class gossip=org.continuent.appia.protocols.group.heal.GossipOutEvent.class;
    Class periodic=PeriodicTimer.class;
    Class hello=HelloEvent.class;
    
    evProvide=new Class[3];
    evProvide[0]=org.continuent.appia.protocols.group.heal.ConcurrentViewEvent.class;
    evProvide[1]=gossip;
    evProvide[2]=hello;
    
    evRequire=new Class[2];
    evRequire[0]=view;
    evRequire[1]=periodic;
    
    evAccept=new Class[7];
    evAccept[0]=view;
    evAccept[1]=gossip;
    evAccept[2]=Debug.class;
    evAccept[3]=periodic;
    evAccept[4]=OtherViews.class;
    evAccept[5]=GroupInit.class;
    evAccept[6]=hello;
  }
  
  public Session createSession() {
    return new HealSession(this,GOSSIP_TIME, HELLO_MIN_TIME);
  }
}