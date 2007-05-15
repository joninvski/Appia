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
package org.continuent.appia.protocols.total.switching;

import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.core.events.channel.EchoEvent;
import org.continuent.appia.protocols.common.RegisterSocketEvent;
import org.continuent.appia.protocols.group.events.GroupInit;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;

public class SwitchingLayer extends Layer {

	public SwitchingLayer() {
		super();
		
		evProvide=new Class[] {
				GroupInit.class,
				SwitchEvent.class,
                View.class,
                BlockOk.class,
                RegisterSocketEvent.class,
				NullEvent.class,
				NullEventTimer.class,
		};
		
		evRequire=new Class[] {
		};
		
		evAccept=new Class[] {
				ChannelInit.class,
                EchoEvent.class,
				View.class,
				BlockOk.class,
				GroupSendableEvent.class,
				Event.class,
                SwitchEvent.class,
				NullEvent.class,
				NullEventTimer.class,
		};
	}

	public Session createSession() {
		return new SwitchingSession(this);
	}

}
