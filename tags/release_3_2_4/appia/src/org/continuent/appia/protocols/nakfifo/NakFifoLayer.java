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
 /*
 * NakFifoLayer.java
 *
 * Created on 10 de Julho de 2003, 15:43
 */

package org.continuent.appia.protocols.nakfifo;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.AppiaMulticastSupport;
import org.continuent.appia.protocols.frag.MaxPDUSizeEvent;
import org.continuent.appia.protocols.common.SendableNotDeliveredEvent;


/** Layer for protocols that provides reliable point-to-point communication.
 * <br>
 * It offers <i>AppiaMulticast</i> support by sending a different message to each
 * destination.
 * @author Alexandre Pinto
 */
public class NakFifoLayer extends Layer implements AppiaMulticastSupport {
  
  /** Creates a new instance of NakFifoLayer */
  public NakFifoLayer() {
    evProvide=new Class[] {
        org.continuent.appia.protocols.nakfifo.NackEvent.class,
        org.continuent.appia.protocols.nakfifo.NakFifoTimer.class,
        org.continuent.appia.protocols.nakfifo.IgnoreEvent.class,
        org.continuent.appia.protocols.nakfifo.PingEvent.class,
        org.continuent.appia.protocols.common.FIFOUndeliveredEvent.class
    };
    
    evRequire=new Class[0];
    
    evAccept=new Class[] {
        org.continuent.appia.protocols.nakfifo.NackEvent.class,
        org.continuent.appia.protocols.nakfifo.NakFifoTimer.class,
        org.continuent.appia.protocols.nakfifo.IgnoreEvent.class,
        org.continuent.appia.protocols.nakfifo.PingEvent.class,
        SendableNotDeliveredEvent.class,
        org.continuent.appia.core.events.SendableEvent.class,
        org.continuent.appia.core.events.channel.ChannelInit.class,
        org.continuent.appia.core.events.channel.ChannelClose.class,
        MaxPDUSizeEvent.class,
        org.continuent.appia.core.events.channel.Debug.class
    };
  }
  
  /** Returns a new NakFifoSession
   * @return a new NakFifoSession
   */  
  public Session createSession() {
    return new NakFifoSession(this);
  }
}
