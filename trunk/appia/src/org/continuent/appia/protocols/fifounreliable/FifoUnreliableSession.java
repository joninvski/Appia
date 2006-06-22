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
package org.continuent.appia.protocols.fifounreliable;

//////////////////////////////////////////////////////////////////////
////
//Appia: protocol development and composition framework            //
////
//Version: 1.0/J                                                   //
////
//Copyright, 2000, Universidade de Lisboa                          //
//All rights reserved                                              //
//See license.txt for further information                          //
////
//Class: FifoUnreliableSession:                                    //
//Fifo ordering for multicast or unicast                           //
////
//Author: Sandra Teixeira, 11/2001                                 //
////
//////////////////////////////////////////////////////////////////////


import java.util.Hashtable;
import java.util.Set;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.TimeProvider;
import org.continuent.appia.core.events.SendableEvent;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.protocols.common.InetWithPort;

/**
 * Class FifoUnreliableSession  
 *
 *
 * @author sandra Teixeira
 * @see    FifoUnreliableLayer
 * @see    Session
 */

public class FifoUnreliableSession extends Session {
	
	private Hashtable peers;
	private int seqnumber; /*just one sequence number*/
	private long refresh;
	private TimeProvider timeProvider = null;
	
	/**
	 *Default session constructor
	 */
	
	public FifoUnreliableSession(Layer l) {
		super(l);
		
		seqnumber=0;
		peers=new Hashtable();
	}
	
	/*checks if the peer exists if not the peer is created*/
	/*treats the events with direction UP, returns true if this is a new message*/
	private boolean checkPeerUP(InetWithPort id, int seq){
		
		PeerInfo peer=(PeerInfo)(peers.get(id));
		
		if(peer!=null)
			return peer.testRecSeq(seq);
		else{
			peer=new PeerInfo(seq,1);
			peers.put((java.lang.Object)id,(java.lang.Object)peer);
			return true;
		} 	    
	}
	
	/*checks if the peer exists if not the peer is created*/
	/*treats the events with direction DOWN, returns the next sequence number*/
	private void checkPeerDOWN(InetWithPort id){
		
		PeerInfo peer=(PeerInfo)(peers.get(id));
		
		if(peer!=null)
			seqnumber=seqnumber+1;
		else{
			peer=new PeerInfo(0,1);
			peers.put((java.lang.Object)id,(java.lang.Object)peer);
			seqnumber=seqnumber+1;
		} 	    
	}
	
	/* The sequence number is pushed on the header.
	 */    
	private void processOutgoing(SendableEvent e) {
		
		checkPeerDOWN((InetWithPort)(e.dest));
		
		e.getMessage().pushInt(seqnumber);
		try {
			e.go();
		}
		catch(AppiaEventException ex) {}
		
		clean();
	}
	
	/*verify if this message is old or new, discard the old message*/
	private void processIncoming(SendableEvent e) {
		
		boolean bool;
		int seq=e.getMessage().popInt();
		bool=checkPeerUP((InetWithPort)(e.source),seq);
		
		if (bool==true){
			try {
				e.go();
			}
			catch(AppiaEventException ex) {
			}
		}
		else{/*old sequence number, the event will be discard*/
			e = null;
		}
		clean();
	}
	
	/*
	 * Checks if it is possible to delete some peer info
	 */
	private void clean(){
		
		long aux = timeProvider.currentTimeMillis();
		
		if ((aux-refresh) < 20000)
			return ;
		
		int size = peers.size();
		int counter=0;
		
		Set set = peers.keySet();
		Object[] obj = set.toArray();
		
		while(counter<size){
			if(((PeerInfo)(peers.get((InetWithPort)obj[counter]))).getcontrol()==0){
				peers.remove((InetWithPort)obj[counter]);
			}
			else
				((PeerInfo)(peers.get((InetWithPort)obj[counter]))).setcontrol(0);
			
			counter++;
		}
		refresh=timeProvider.currentTimeMillis();
	}
	
	/**
	 * Main Event handler function. Accepts incoming  sendableEvent and
	 * dispatches them to the appropriate functions
	 * @param e The incoming event
	 * @see Session
	 */
	
	public void handle(Event e) {
		
		if(e instanceof SendableEvent)
			handleSendable((SendableEvent)e);
		else {
			if( e instanceof ChannelInit){
				timeProvider = e.getChannel().getTimeProvider();
				refresh = timeProvider.currentTimeMillis();
			}
			try {
				e.go();
			}
			catch(AppiaEventException ex) {}
		}
	}
	
	private void handleSendable(SendableEvent e) {
		
		switch(e.getDir()) {
		case Direction.UP :
			processIncoming(e);
			break;
		case Direction.DOWN :
			processOutgoing(e);
			break;
		default:
		}
	}
}
