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
 * Title:        Appia<p>
 * Description:  Protocol development and composition framework<p>
 * Copyright:    Copyright (c) Nuno Carvalho and Luis Rodrigues<p>
 * Company:      F.C.U.L.<p>
 * @author Alexandre Pinto and Hugo Miranda
 * @version 1.0
 */
package org.continuent.appia.protocols.group.remote;

import org.continuent.appia.core.*;
import org.continuent.appia.protocols.group.heal.GossipOutLayer;


/**
 * This layer describes the GossipOutExt  facilty provided by GossipOutExt.
 */
public class RemoteGossipOutLayer extends GossipOutLayer {
	
	/**
	 * Standard constructor.
	 */
	public RemoteGossipOutLayer() {
		Class init = org.continuent.appia.core.events.channel.ChannelInit.class;
		Class close = org.continuent.appia.core.events.channel.ChannelClose.class;
		Class gossipout = org.continuent.appia.protocols.group.heal.GossipOutEvent.class;
		Class rse = org.continuent.appia.protocols.common.RegisterSocketEvent.class;
		Class view = org.continuent.appia.protocols.group.intra.View.class;
		Class debug = org.continuent.appia.core.events.channel.Debug.class;
		Class groupinit =	org.continuent.appia.protocols.group.events.GroupInit.class;
		Class undelivered = org.continuent.appia.protocols.common.FIFOUndeliveredEvent.class;
		Class remote = org.continuent.appia.protocols.group.remote.RemoteViewEvent.class;
		
		evProvide=new Class[4];
		evProvide[0] = gossipout;
		evProvide[1] = rse;
		evProvide[2] = remote;
		evProvide[3] = debug;
		
		
		evRequire=new Class[0];
		
		evAccept=new Class[8];
		evAccept[0] = gossipout;
		evAccept[1] = init;
		evAccept[2] = close;
		evAccept[3] = view;
		evAccept[4] = groupinit;
		evAccept[5] = remote;
		evAccept[6] = debug;
		evAccept[7] = undelivered;
	}
	
	
	/**
	 * Creates a {@link RemoteGossipOutSession}
	 *
	 * @return the created session.
	 */
	public Session createSession() {
		return new RemoteGossipOutSession(this);
	}
}










