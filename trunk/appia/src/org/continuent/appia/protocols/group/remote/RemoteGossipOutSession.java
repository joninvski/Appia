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

/*
 * New version of this protocol that reuses the
 * methods implemented in the father of this session (GossipOutSession) 
 */
 
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.group.Group;
import org.continuent.appia.protocols.group.ViewState;
import org.continuent.appia.protocols.group.events.GroupInit;
import org.continuent.appia.protocols.group.heal.GossipOutSession;


/**
 * Extends the functionality of the 
 * {@link org.continuent.appia.protocols.group.heal.GossipOutSession} by giving membership
 * information to non-members of the group.
 */
public class RemoteGossipOutSession extends GossipOutSession {

    private static Logger log = Logger.getLogger(RemoteGossipOutSession.class);

	private Group group;

	/**
	 * Default Constructor.
	 * @param layer The {@link org.continuent.appia.core.Layer} associated to this session.
	 */
	public RemoteGossipOutSession(Layer layer) {
		super(layer);
	}

	/**
	 * Main handler.
	 * @param event The received event.
	 */
	public void handle(Event event) {
		if (event instanceof GroupInit) {
			group = ((GroupInit) event).vs.group;
		} else if (event instanceof RemoteViewEvent) {
			handleRemoteView((RemoteViewEvent) event);
			return;
		}
		super.handle(event);
	}

	private void handleRemoteView(RemoteViewEvent ev) {

		try {
			InetSocketAddress sourceAddr = (InetSocketAddress) ev.getMessage().popObject(); 
			//if this process is a member of the desired group
			Group g = Group.pop((Message)ev.getMessage());
			if(log.isDebugEnabled())
				log.debug("Received remote view event from "+sourceAddr+" with group "+g+" ("+group+")");
			if (!group.equals(g)) {
				return;
			}

			ev.source = getOutAddress();
			ev.dest = sourceAddr;

			Message om = new Message();
			ViewState.push(getViewState(),om);
			//om.push(getViewState());

			if(log.isDebugEnabled())
				log.debug("Sendig RemoteView to "+sourceAddr+" : "+getViewState());
			ev.setMessage(om);
			ev.setChannel(getOutChannel());
			ev.setDir(Direction.DOWN);
			ev.setSource(this);

			ev.init();
			ev.go();

		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			System.out.println("ERROR sending event");
		}
	}
}
