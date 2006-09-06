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
package org.continuent.appia.protocols.group.intra;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.Debug;
import org.continuent.appia.core.events.channel.EchoEvent;
import org.continuent.appia.protocols.group.events.GroupInit;
import org.continuent.appia.protocols.group.suspect.Fail;


public class IntraLayer extends Layer {
  
  public IntraLayer() {
    Class view=org.continuent.appia.protocols.group.intra.View.class;
    Class install=org.continuent.appia.protocols.group.intra.InstallView.class;
    Class init=GroupInit.class;
    Class preview=org.continuent.appia.protocols.group.intra.PreView.class;
    Class newview=org.continuent.appia.protocols.group.intra.NewView.class;
    
    evProvide=new Class[5];
    evProvide[0]=view;
    evProvide[1]=install;
    evProvide[2]=EchoEvent.class;
    evProvide[3]=preview;
    evProvide[4]=newview;
    
    evRequire=new Class[1];
    evRequire[0]=init;
    
    evAccept=new Class[8];
    evAccept[0]=install;
    evAccept[1]=Fail.class;
    evAccept[2]=init;
    evAccept[3]=view;
    evAccept[4]=org.continuent.appia.protocols.group.intra.ViewChange.class;
    evAccept[5]=preview;
    evAccept[6]=newview;
    evAccept[7]=Debug.class;
  }
  
  public Session createSession() {
    return new IntraSession(this);
  }
}