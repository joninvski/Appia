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


package org.continuent.appia.protocols.total.token;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelClose;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;


/**
 * Layer that provides regular Total Order of events. This layer requires the
 * virtual synchrony protocols in order to work.
 * @author Nuno Carvalho
 *
 */
public class TotalTokenLayer extends Layer {

	public TotalTokenLayer() {
		super();
		evProvide = new Class[]{
				TokenEvent.class,
                TokenTimer.class,
		};
		evRequire = new Class[]{
		        GroupSendableEvent.class,
                View.class,
        };
		evAccept = new Class[]{
				TokenEvent.class,
				GroupSendableEvent.class,
				BlockOk.class,
				View.class,
				ChannelInit.class,
				ChannelClose.class,
                TokenTimer.class,
		};
	}

	public Session createSession() {
		return new TotalTokenSession(this);
	}

}
