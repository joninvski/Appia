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
package org.continuent.appia.protocols.totalSequencer;

import org.continuent.appia.core.*;
import org.continuent.appia.protocols.totalAbstract.*;


/**
 * Defines the events that the layer accepts, provides and requires
 */
public class TotalSequencerLayer extends TotalAbstractLayer {
	
	/** Constructor*/
	public TotalSequencerLayer() {
		
		evRequire=new Class[3];
		evRequire[0]=org.continuent.appia.protocols.group.events.GroupSendableEvent.class;
		evRequire[1]=org.continuent.appia.protocols.group.intra.View.class;
		evRequire[2]=org.continuent.appia.protocols.totalSequencer.TotalOrderEvent.class;
		
		evAccept=new Class[5];
		evAccept[0]=evRequire[0];
		evAccept[1]=org.continuent.appia.core.events.channel.ChannelInit.class;
		evAccept[2]=evRequire[1];
		evAccept[3]=evRequire[2];
		evAccept[4]=org.continuent.appia.protocols.group.sync.BlockOk.class;
		evProvide=new Class[1];
		evProvide[0]= evRequire[2];
	}
	
	/** Creates a new session of this layer*/
	public Session createSession() {
		return new TotalSequencerSession(this);
	}
	
	public void channelDispose(Session session,Channel channel) {
	}
}




