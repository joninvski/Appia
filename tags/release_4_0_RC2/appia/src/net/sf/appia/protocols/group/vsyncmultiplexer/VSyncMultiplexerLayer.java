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
package net.sf.appia.protocols.group.vsyncmultiplexer;

import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.events.channel.EchoEvent;
import net.sf.appia.protocols.group.intra.View;
import net.sf.appia.protocols.group.sync.BlockOk;



/**
 * @author nunomrc
 */
public class VSyncMultiplexerLayer extends Layer {

	public VSyncMultiplexerLayer(){
		
		evProvide = new Class[]{
		};
		
		evRequire = new Class[]{
			View.class,
			BlockOk.class,
		};
		
		evAccept = new Class[]{
			EchoEvent.class,
			View.class,
			BlockOk.class,
			ChannelInit.class,
			ChannelClose.class,
		};
	}
	
	/* (non-Javadoc)
	 * @see appia.Layer#createSession()
	 */
	public Session createSession() {
		return new VSyncMultiplexerSession(this);
	}

}
