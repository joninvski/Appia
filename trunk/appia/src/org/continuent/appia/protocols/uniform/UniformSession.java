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

import org.apache.log4j.Logger;
import org.continuent.appia.core.AppiaError;
import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.AppiaException;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.events.Send;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.SessionProperties;


public class UniformSession extends Session implements InitializableSession {
	
	Logger log = Logger.getLogger(UniformSession.class);

	//private Channel channel;

	private long seqNumber, myRank;

	private int max_nodes, majority_nodes;

	private static final String MAX_NODES = "max_nodes";
	private static final String MAJ_NODES = "majority_nodes";

	private UniformData uniformData;

	//private ViewState vs;

	//private LocalState ls;

	public UniformSession(Layer l) {
		super(l);

		uniformData = new UniformData();
	}

	/**
	 * Called by the AppiaXML parser to deliver parameters to this session. 
	 * It accepts the parameters "max_nodes" or "majority_nodes". First, "max_nodes" is
	 * readed and only if this parameter does not exist "majority_nodes" is readed. Only one
	 * is needed. If the two parameters are given, the "majority_nodes" is ignored.
	 * @see org.continuent.appia.xml.interfaces.InitializableSession#init(org.continuent.appia.xml.utils.SessionProperties)
	 */
	public void init(SessionProperties params) {
		if (params.containsKey(MAX_NODES)){
			max_nodes = params.getInt(MAX_NODES);
			majority_nodes = (max_nodes/2)+1;
			return;
		}
		else if (params.containsKey(MAJ_NODES)) {
			majority_nodes = params.getInt(MAJ_NODES);
			max_nodes = Integer.MAX_VALUE;
			return;
		}
		else
            // FIXME: add a proper exception here.
			throw new AppiaError("parameter " + MAX_NODES + " or " + MAJ_NODES
					+ " must be set in the xml config file (Session: "
					+ this.getClass().getName() + ").");
	}

	/**
	 * Main handler of events.
	 */
	public void handle(Event e) {
		if(log.isDebugEnabled())
			log.debug("MAIN Uniform Handle: "+e + " Direction is "+(e.getDir()==Direction.DOWN? "DOWN" : "UP"));
		
		if (e instanceof ChannelInit)
			handleChannelInit((ChannelInit) e);

		else if (e instanceof UniformAckEvent)
			handleUniformAckEvent((UniformAckEvent) e);

		else if (e instanceof GroupSendableEvent)
			handleGroupSendable((GroupSendableEvent) e);

		else if (e instanceof BlockOk)
			handleBlockOk((BlockOk) e);

		else if (e instanceof View)
			handleView((View) e);

		else if(log.isDebugEnabled())
			log.debug("Unknown event: "+e);
	}

	private void handleGroupSendable(GroupSendableEvent e) {
		if (e instanceof Send) {
			try {
				e.go();
			} catch (AppiaException ex) {
				ex.printStackTrace();
				log.debug("ERROR sending event.");
			}
		} else {
			if (e.getDir() == Direction.DOWN)
				handleGroupSendableDown(e);
			else
				handleGroupSendableUp(e);
		}
	}

	private void handleGroupSendableDown(GroupSendableEvent e) {

		UniformHeader uh = new UniformHeader(seqNumber++, myRank);

		e.getMessage().pushObject(uh);

		try {
			e.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			log.debug("Error sending event");
		}
	}

	private void handleGroupSendableUp(GroupSendableEvent e) {

		UniformHeader uh = (UniformHeader) e.getMessage().popObject();
		UniformNode node = null;

		if (!uniformData.exists(uh)) {
			if(log.isDebugEnabled())
				log.debug("Uniform data does not exist. Creating it -> "+uh);
			node = new UniformNode(uh, e);
			uniformData.put(node);
		} else {
			node = uniformData.get(uh);
			if(log.isDebugEnabled())
				log.debug("Uniform data exists. event of uniform data is "+node.getEvent());
			if (node.getEvent() == null)
				node.setEvent(e);
		}

		//send ack to the group garanteing that it has seen the message.
		UniformAckEvent uae = null;
		try {
			uae = new UniformAckEvent(e.getChannel(), Direction.DOWN, this,
					e.group, e.view_id);
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			log.debug("Error sending event");
		}

		uae.getMessage().pushObject(uh);
		if(log.isDebugEnabled())
			log.debug("Sending Uniform ACK.");
		try {
			uae.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			log.debug("Error Sending Event");
		}
	}

	private void handleUniformAckEvent(UniformAckEvent e) {
		UniformHeader header;
		UniformNode deliver, node;

		header = (UniformHeader) e.getMessage().popObject();

		if (!uniformData.exists(header)) {
			if(log.isDebugEnabled())
				log.debug("data does not exist. creating it. -> "+header);
			node = new UniformNode(header, null);
			uniformData.put(node);
		} else {
			if(log.isDebugEnabled())
				log.debug("data exists. updating it. -> "+header);
			deliver = uniformData.update(header, majority_nodes);
			if(log.isDebugEnabled())
				log.debug("data updated. -> "+deliver);
			if (deliver != null && deliver.getEvent() != null){
				if(log.isDebugEnabled())
					log.debug("delivering message. -> "+deliver.getEvent());
				try {
					deliver.getEvent().go();
				} catch (AppiaEventException ex) {
					ex.printStackTrace();
					log.debug("Error sending the event to the upper layers");
				}
			}
		}
	}

	private void handleBlockOk(BlockOk e) {
		try {
			e.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			log.debug("Error sending blockOk");
		}
	}

	private void handleView(View e) {
		//vs = e.vs;
		//ls = e.ls;

		// TODO: must acknoledge messages (and deliver it) in the next view!
		// Cleaning messages on view change is wrong!!!
		if(uniformData.size() > 0)
			uniformData.clean();

		try {
			e.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			log.debug("Error sending view");
		}
	}

	private void handleChannelInit(ChannelInit e) {
		//channel = e.getChannel();

		try {
			e.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			log.debug("Error sending channelinit");
		}
	}

}