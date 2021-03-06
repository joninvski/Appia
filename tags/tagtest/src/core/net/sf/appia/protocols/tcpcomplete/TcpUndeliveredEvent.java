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
 package net.sf.appia.protocols.tcpcomplete;

import net.sf.appia.core.*;
import net.sf.appia.protocols.common.NetworkUndeliveredEvent;



/**
 * Event used to sinalize upper layers that a connection was closed.
 */
public class TcpUndeliveredEvent extends NetworkUndeliveredEvent {
    
    public TcpUndeliveredEvent (Channel channel, int dir, Session session, Object who) 
    throws AppiaEventException {
        super(channel, dir, session);
        setFailedAddress(who);
    }
    
    public TcpUndeliveredEvent(Object who) {
        super();
        setFailedAddress(who);
    }  
}
