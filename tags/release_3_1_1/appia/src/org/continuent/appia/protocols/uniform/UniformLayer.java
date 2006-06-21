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
 package org.continuent.appia.protocols.uniform;

import org.continuent.appia.core.*;

/**
 * The Uniform is the protocol that assures that the messages
 * are seen by all members of the group before being delivered.
 */
public class UniformLayer extends Layer {

	/*
	 * Basic Constructor.
	 */
	public UniformLayer() {

		// Events that the protocol requires
		evRequire = new Class[] {
				org.continuent.appia.protocols.group.events.GroupSendableEvent.class,
				org.continuent.appia.protocols.group.intra.View.class, };

		//Events that the protocol accepts
		evAccept = new Class[] {
				org.continuent.appia.protocols.group.events.GroupSendableEvent.class,
				org.continuent.appia.core.events.channel.ChannelInit.class,
				org.continuent.appia.protocols.group.intra.View.class,
				org.continuent.appia.protocols.group.sync.BlockOk.class,
				org.continuent.appia.protocols.uniform.UniformAckEvent.class, };

		//Events that the protocol provides
		evProvide = new Class[] {
				org.continuent.appia.protocols.uniform.UniformAckEvent.class, };
	}

	public Session createSession() {
		return new UniformSession(this);
	}

}