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
package net.sf.appia.project.debug.counter;

import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelClose;


/**
 * This class defines a Counter Layer
 * 
 * @author João Trindade
 * @version 1.0
 */
public class CounterLayer extends Layer {

    /**
     * Creates a new EccoLayer.
     */
	public CounterLayer() {
		
		evRequire = new Class[]{
                SendableEvent.class,
		};
        
		evProvide = new Class[] {
                SendableEvent.class,
        };
		
		evAccept = new Class[]{
                ChannelClose.class,
                SendableEvent.class,
        };
	}
	
	/**
     * Creates the session for this protocol.
	 * @see Layer#createSession()
	 */
	public Session createSession() {
		return new CounterSession(this);
	}
}
