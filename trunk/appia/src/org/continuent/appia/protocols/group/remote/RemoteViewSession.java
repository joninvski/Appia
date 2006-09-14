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
 * @author Nuno Carvalho and Luis Rodrigues
 * @version 1.0
 */
package org.continuent.appia.protocols.group.remote;

/*
 * Change Log:
 * Nuno Carvalho - 03/03/2003
 * Changed the push and pop of InetWithPort:
 * from om.push(addrs) -> InetWithPort.push(sddrs,om)
 */


import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.continuent.appia.core.AppiaCursorException;
import org.continuent.appia.core.AppiaDuplicatedSessionsException;
import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.AppiaInvalidQoSException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.ChannelCursor;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.EventQualifier;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.QoS;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.core.events.channel.Debug;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.common.RegisterSocketEvent;
import org.continuent.appia.protocols.fifo.FifoLayer;
import org.continuent.appia.protocols.fifo.FifoSession;
import org.continuent.appia.protocols.group.Endpt;
import org.continuent.appia.protocols.group.Group;
import org.continuent.appia.protocols.group.ViewID;
import org.continuent.appia.protocols.group.ViewState;
import org.continuent.appia.protocols.group.heal.GossipOutEvent;
import org.continuent.appia.protocols.udpsimple.UdpSimpleLayer;
import org.continuent.appia.protocols.udpsimple.UdpSimpleSession;
import org.continuent.appia.protocols.utils.ParseUtils;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.SessionProperties;


/**
 * RemoteViewSession is the session that implements the RemoteView request 
 * functionality.<p> 
 *
 * It registers itself in the specified gossip server.
 * The requests are made by sending a {@link RemoteViewEvent} to this session
 * with the required group id, witch are replied by an ascending 
 * RemoteViewEvent containing the group id and an array of the group member's
 * addresses.
 */
public class RemoteViewSession extends Session implements InitializableSession {
	
    private static Logger log = Logger.getLogger(RemoteViewSession.class);
    private static final boolean FULL_DEBUG = false;
    private PrintStream debug = null;

	public static final int DEFAULT_GOSSIP_PORT = 10000;
	
	private InetSocketAddress myAddress=null;
	private InetSocketAddress gossipAddress;
	private Hashtable addrTable = new Hashtable();
	private Channel myChannel = null;
	private Channel initChannel;
	private boolean needsRse = true;
	private RemoteViewEvent rve=null;
	
	/**
	 * Session standard constructor.
	 */
	public RemoteViewSession(Layer l) {
		super(l);
	}
	
	
	/**
	 * Initializes this session. Must be called before the channel is started.
	 *
	 * @param gossipAddress the address of the gossip server to contact
	 */
	public void init(InetSocketAddress gossipAddress) {
		this.gossipAddress = gossipAddress;
	}
	
	public void init(SessionProperties params) {
		if (params.containsKey("gossip")) {
			try {
				gossipAddress = ParseUtils.parseSocketAddress(params.getString("gossip"),null,DEFAULT_GOSSIP_PORT);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	/**
	 * The event handler method.
	 *
	 * @param event the event
	 */
	public void handle(Event event) {
		
		debug("received " + event + ", instanceof RemoteViewEvent=" + (event instanceof RemoteViewEvent));
		
		if (event instanceof ChannelInit) {
			handleChannelInit((ChannelInit) event);
			return;
		}
		
		if (event instanceof RemoteViewEvent) {
			handleRemoteView((RemoteViewEvent) event);
			return;
		}
		
		if (event instanceof GossipOutEvent) {
			handleGossipOut((GossipOutEvent) event);
			return;
		}
		
		if(event instanceof RegisterSocketEvent){
			handleRegisterSocketEvent((RegisterSocketEvent) event);
			return;
		}
		
		if (event instanceof Debug) {
			Debug ev = (Debug) event;
			
			if (ev.getQualifierMode() == EventQualifier.ON) {
				if (ev.getOutput() instanceof PrintStream)
					debug = (PrintStream)ev.getOutput();
				else
					debug = new PrintStream(ev.getOutput());
				log.debug("Full debugging started.");
			} else {
				if (ev.getQualifierMode() == EventQualifier.OFF)
					debug = null;
				else if (ev.getQualifierMode() == EventQualifier.NOTIFY) {
					if (ev.getOutput() instanceof PrintStream)
						debug = (PrintStream)ev.getOutput();
					else
						debug = new PrintStream(ev.getOutput());
					printAddrTable();
					debug = null;
				}
			}
			
			try { ev.go(); } 
			catch (AppiaEventException ex) { ex.printStackTrace(); }
			return;
		}
		
		log.warn("Received unwanted event (" + event.getClass().getName() +
		") received. Forwarding it.");
		try { event.go(); } 
		catch (AppiaEventException ex) { ex.printStackTrace(); }
	}
	
	
	private void handleChannelInit(ChannelInit event) {
		
		try { event.go(); }
		catch (AppiaEventException ex) {
			ex.printStackTrace();
			log.debug("error forwarding ChannelInit event: "+ex);
		}
		
		if (myChannel == null) {
			initChannel = event.getChannel();
			makeOutChannel(initChannel);
		} else {
			if (needsRse) {
				try {
					RegisterSocketEvent rse =
						new RegisterSocketEvent(myChannel,
								Direction.DOWN,
								this,
								myAddress.getPort());
					rse.go();
				} catch (AppiaEventException ex) {
					log.debug("error registering socket: "+ex);
				}
			}	    
			
			// send a GossipOutEvent to set things in motion immediatly
			try {
				
				GossipOutEvent goe =
					new GossipOutEvent(myChannel,Direction.DOWN,
							this);
				Message om = (Message) goe.getMessage();
				om.pushObject(new ViewID(0, new Endpt()));
				om.pushObject(new Group());
				om.pushObject(myAddress);
				goe.source = myAddress;
				goe.dest = gossipAddress;
				goe.init();
				goe.go();
			} catch (AppiaEventException ex) {
				log.debug("error sending initial GossipOutEvent: "+ex);
			}
		}
	}
	
	
	private void makeOutChannel(Channel t) {
		
		ChannelCursor cct = new ChannelCursor(t);
		
		Layer[] l = new Layer[3];
		
		try {
			cct.bottom();
			if (cct.getLayer() instanceof UdpSimpleLayer) {
				l[0] = cct.getLayer();
				needsRse = false;
			} else {
				l[0] = new UdpSimpleLayer();
				needsRse = true;
			}
			
			while(cct.isPositioned() && !(cct.getLayer() instanceof FifoLayer))
				cct.up();
			if (cct.isPositioned())
				l[1] = cct.getLayer();
			else
				l[1] = new FifoLayer();
			
			l[2] = this.getLayer();

			QoS qos = new QoS("Gossip Out QoS", l);
			myChannel = qos.createUnboundChannel("Gossip Channel",t.getEventScheduler());
			
			ChannelCursor mycc = myChannel.getCursor();
			mycc.bottom();
			cct.bottom();
			
			if (cct.getSession() instanceof UdpSimpleSession)
				mycc.setSession(cct.getSession());
			
			mycc.up();
			
			while (cct.isPositioned() && 
					!(cct.getSession() instanceof FifoSession))
				cct.up();
			if (cct.isPositioned())
				mycc.setSession(cct.getSession());
			
			mycc.up();
			mycc.setSession(this);
			
			myChannel.start();
		} catch (AppiaCursorException ex) {
			log.debug("Error: unable to create GossipOut channel: "+ex);
		} catch (AppiaInvalidQoSException ex) {
			log.debug("Error: unable to create GossipOut channel: "+ex);
		} catch (AppiaDuplicatedSessionsException ex) {
			log.debug("Error: unable to create GossipOut channel: "+ex);
		}
	}
		
	private void handleGossipOut(GossipOutEvent event) {
		
		try{
			event.go();
		}
		catch(AppiaEventException e){
			e.printStackTrace();
			System.out.println("Error sending event");
		}
	}
	
	
	private void handleRemoteView(RemoteViewEvent event) {
		if (event.getDir() == Direction.DOWN) {
			// we got a request
			
			if (myAddress==null) {
				rve=event;
				return;
			}
			
			try {
				if(log.isDebugEnabled())
					log.debug("sending request from " + myAddress + 
						" to " + gossipAddress + " in channel " + myChannel.getChannelID() +
						" for group " + event.getGroup());

				event.dest = gossipAddress;
				event.source = myAddress;
				
				Message om = new Message();
				Group.push(event.getGroup(),om);
				om.pushObject(myAddress);
				
				event.setMessage(om);
				event.setChannel(myChannel);
				event.setSource(this);
				
				event.init();
				event.go();
				
				event = null;
			} catch (AppiaEventException ex) {
				log.debug("error sending down RemoteViewEvent");
			}
		} else {
			boolean appearsViewState = true;
			Message om = (Message) event.getMessage();
			try{
				ViewState.peek(om);
			}catch(Exception special){
				appearsViewState = false;
			}
			//debug("Received remote view event!!!!!("+ViewState.peek(om)+")");
			if (appearsViewState && ViewState.peek(om) instanceof ViewState) {
				ViewState receivedVs = ViewState.pop(om);
				event.setAddresses(receivedVs.addresses);	    
				event.setGroup(receivedVs.group);
				event.setSource(this);
				event.setChannel(initChannel);
				
				try {
					event.init();
					event.go();
				} catch(AppiaEventException ex){
					ex.printStackTrace();
					System.out.println();
				}
			}
		}
	}
	
	
	private void handleRegisterSocketEvent(RegisterSocketEvent e){
		if(e.getDir()==Direction.UP){
			myAddress = new InetSocketAddress(e.localHost, e.port);
			
			if(rve!=null){
				handle(rve);
				rve=null; 
			}
		}
		
		try{
			e.go();
		}
		catch(AppiaEventException ex){
			ex.printStackTrace();
			System.err.println("Exception when sending RegisterSocket event");
		}
	}
	
	/**
	 * Sends a Debug event through the Gossip Out channel with the specified
	 * EventQualifier
	 *
	 * @see Debug
	 * @see EventQualifier
	 */
	public void doDebug(int eq) { 
		try {
			java.io.OutputStream debugOut = System.out;
			
			Debug e = new Debug(debugOut);
			e.setChannel(myChannel);
			e.setDir(Direction.DOWN);
			e.setSource(this);
			e.setQualifierMode(eq);
			e.init();
			e.go();
		} catch (AppiaEventException ex) {
			ex.printStackTrace();
			System.err.println("Exception when sending debug event");
		}
	}
		
	private void debug(String s){
		if(FULL_DEBUG && debug != null)
			debug.println(this.getClass().getName()+"[FULL DEBUG] "+s);
	}
	
	private void printAddrTable() {
		debug("address Table:");
		for (Enumeration e = addrTable.keys(); e.hasMoreElements(); ) {
			String g = (String) e.nextElement();
			InetSocketAddress ad = (InetSocketAddress) addrTable.get(g);
			debug("{" + g + "=" + ad + "}");
		}
	}
}
