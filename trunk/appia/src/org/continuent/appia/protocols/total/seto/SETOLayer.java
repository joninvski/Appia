
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
 * Initial developer(s): Nuno Carvalho and Jose' Mocito.
 * Contributor(s): See Appia web page for a list of contributors.
 */
package org.continuent.appia.protocols.total.seto;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelClose;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;
import org.continuent.appia.protocols.total.common.RegularServiceEvent;
import org.continuent.appia.protocols.total.common.SETOServiceEvent;
import org.continuent.appia.protocols.total.common.UniformServiceEvent;

/**
 * @author nunomrc
 *
 */
public class SETOLayer extends Layer {

	public SETOLayer(){
		super();
		evAccept = new Class[]{
				ChannelInit.class,
				ChannelClose.class,
				GroupSendableEvent.class,
				View.class,
				BlockOk.class,
				SeqOrderEvent.class,
				SETOTimer.class,
				UniformTimer.class,
				UniformInfoEvent.class,
		};
		
		evRequire = new Class[]{};
		
		evProvide = new Class[]{
				UniformServiceEvent.class,
				RegularServiceEvent.class,
				SETOServiceEvent.class,
		};
	}
	
	/* (non-Javadoc)
	 * @see appia.Layer#createSession()
	 */
	public Session createSession() {
		return new SETOSession(this);
	}

}
