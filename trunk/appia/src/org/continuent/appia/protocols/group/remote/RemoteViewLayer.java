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

/**
 * This layer describes the Remote View facilty provided by RemoteViewSession.
 */
public class RemoteViewLayer extends Layer {
	
	/**
	 * Standard constructor
	 */
	public RemoteViewLayer() {
	    evProvide = new Class[]{
	            org.continuent.appia.protocols.group.remote.RemoteViewEvent.class,
	            org.continuent.appia.protocols.common.RegisterSocketEvent.class,
	            org.continuent.appia.core.events.channel.Debug.class,
	            org.continuent.appia.protocols.group.heal.GossipOutEvent.class,
	    };
        
		evRequire = new Class[] {};
		
		evAccept = new Class[] {
		        org.continuent.appia.core.events.channel.ChannelInit.class,
		        org.continuent.appia.protocols.group.remote.RemoteViewEvent.class,
		        org.continuent.appia.core.events.channel.Debug.class,
		        org.continuent.appia.protocols.common.RegisterSocketEvent.class,
		};
	}
	
	
	/**
	 * Creates a {@link RemoteViewSession}
	 */
	public Session createSession() {
		return new RemoteViewSession(this);
	}
}
