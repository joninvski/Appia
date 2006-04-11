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

import java.util.LinkedList;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.continuent.appia.core.AppiaError;
import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.SendableEvent;
import org.continuent.appia.core.events.channel.ChannelClose;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.group.LocalState;
import org.continuent.appia.protocols.group.ViewState;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.events.Send;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.SessionProperties;


/**
 * Implementation of a token based total order protocol. 
 * @author Nuno Carvalho
 *
 */
public class TotalTokenSession extends Session implements InitializableSession {

	private static Logger log = Logger.getLogger(TotalTokenSession.class);

	private static final int DEFAULT_NUM_MESSAGES_PER_TOKEN = 10;
	
	private long globalSeqNumber;
	private LinkedList pendingMessages, undeliveredMessages;
	private int rankWidthToken, numMessagesPerToken;
	
	private LocalState localState;
	private ViewState viewState;
	private boolean isBlocked;
	
	
	public TotalTokenSession(Layer layer) {
		super(layer);
		
		pendingMessages = new LinkedList();
		undeliveredMessages = new LinkedList();
		rankWidthToken = 0;
		numMessagesPerToken = DEFAULT_NUM_MESSAGES_PER_TOKEN;
		isBlocked = true;
	}

	public void init(SessionProperties params) {
		if(params.containsKey("num_messages_per_token"))
			numMessagesPerToken = params.getInt("num_messages_per_token");
		if(numMessagesPerToken <= 0)
			numMessagesPerToken = DEFAULT_NUM_MESSAGES_PER_TOKEN;
	}

	public void handle(Event event){
		if((event instanceof GroupSendableEvent) || (event instanceof TokenEvent))
			handleGroupSendable((GroupSendableEvent) event);
		else if (event instanceof BlockOk)
			handleBlock((BlockOk)event);
		else if (event instanceof View)
			handleView((View)event);
		else if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit)event);
		else if (event instanceof ChannelClose)
			handleChannelClose((ChannelClose)event);
		else
			try {
				event.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
	}

	private void handleChannelClose(ChannelClose close) {
		isBlocked = true;
		try {
			close.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	private void handleChannelInit(ChannelInit init) {
		try {
			init.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	private void handleView(View view) {
		localState = view.ls;
		viewState = view.vs;
		rankWidthToken = 0;
		globalSeqNumber = 0;
		isBlocked = false;
		
		if(log.isDebugEnabled())
			log.debug("Received new view with "+viewState.addresses.length+" members");
		
		try {
			view.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
		if(iHaveToken())
			sendMessages(view.getChannel());
	}

	private void handleBlock(BlockOk ok) {
		isBlocked = true;
		try {
			ok.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	private void handleGroupSendable(GroupSendableEvent event) {
		// do not ensure total ordering of point to point events
		if(event instanceof Send){
			try {
				event.go();
			} catch (AppiaEventException e1) {
				e1.printStackTrace();
			}
			return;
		}
			
		if(log.isDebugEnabled())
			log.debug("My rank = "+localState.my_rank+" hasToken = "+rankWidthToken);
		// event from aplication
		if(event.getDir() == Direction.DOWN){
			if(log.isDebugEnabled())
				log.debug("Received Group Sendable from Appl "+event);
			pendingMessages.addLast(event);
			if(iHaveToken() && !isBlocked){
				sendMessages(event.getChannel());
			}
		}
		// event from network
		else {
			long seq = event.getMessage().popLong();
			
			if(seq <= globalSeqNumber){
				throw new AppiaError("Received message with seq = "+seq+" was expecting seq = "+(globalSeqNumber+1));
			}
			
//			if(event.orig != rankWidthToken){
//			throw new AppiaError("Received message from rank "+event.orig+" was expecting "+rankWidthToken);
//			}
			
			if(seq > (globalSeqNumber + 1)){
				storeUndelivered(event,seq);
				if(log.isDebugEnabled())
					log.debug("Message out of order. Storing message with seq = "+seq);
				return;
			}
			
			boolean hasToken = event.getMessage().popBoolean();
			if(log.isDebugEnabled())
				log.debug("Received Group Sendable from the network with seq = "+seq+" token = "+hasToken);
			
			if (!(event instanceof TokenEvent)) {
				try {
					event.go();
				} catch (AppiaEventException e) {
					e.printStackTrace();
				}
			}
			
			if(log.isDebugEnabled())
				log.debug("Delivering message with seq = "+seq);
			
			// at this point, this is the same as: globalSeqNumber = globalSeqNumber +1
			globalSeqNumber = seq;
			
			if(hasToken)
				rotateToken();
			
			while(undeliveredMessages.size() > 0){
				GroupSendableEvent auxEvent = (GroupSendableEvent) undeliveredMessages.getFirst();
				long seqaux = auxEvent.getMessage().peekLong();
				if(seqaux == (globalSeqNumber + 1)){
					undeliveredMessages.removeFirst();
					auxEvent.getMessage().popLong();
					boolean auxHasToken = auxEvent.getMessage().popBoolean();
					if(!(auxEvent instanceof TokenEvent)){
						try {
							auxEvent.go();
						} catch (AppiaEventException e) {
							e.printStackTrace();
						}
						if(log.isDebugEnabled())
							log.debug("Delivering stored message with seq = "+seqaux);
					} else if(log.isDebugEnabled())
						log.debug("Ignored token event with seq = "+seqaux);

					globalSeqNumber = seqaux;
					if(auxHasToken)
						rotateToken();
				}
				else
					break;
			}
			
			if(iHaveToken() && !isBlocked)
				sendMessages(event.getChannel());
		}
	}
	
	/*
	 * Support methods
	 */
	
	private boolean iHaveToken(){
		return (rankWidthToken == localState.my_rank);
	}
	
	private void rotateToken(){
		if(viewState.addresses.length > 1)
			rankWidthToken = ((rankWidthToken+1) == viewState.addresses.length)? 0 : rankWidthToken+1;
	}

	private void sendMessages(Channel channel) {
		if(log.isDebugEnabled())
			log.debug("I'll try to send some messages");

		int listSize = pendingMessages.size();
		if(listSize == 0){
			if(log.isDebugEnabled())
				log.debug("I do not have any messages. Rotanting token. My rank is "+localState.my_rank);
			try {
				TokenEvent token = new TokenEvent(channel,Direction.DOWN,this,viewState.group,viewState.id);
				token.getMessage().pushBoolean(true);
				token.getMessage().pushLong(++globalSeqNumber);
				token.go();
				rotateToken();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
			return;
		}
		boolean sendToken = false;
		for(int i=0; !sendToken; i++){
			if((i+1) == listSize || (i+1) ==  numMessagesPerToken)
				sendToken = true;
			GroupSendableEvent ev = (GroupSendableEvent) pendingMessages.removeFirst();
			ev.orig = localState.my_rank;
			try {
				// Deliver my message
				GroupSendableEvent clone = (GroupSendableEvent) ev.cloneEvent();
				clone.setDir(Direction.invert(ev.getDir()));
				clone.setSource(this);
				clone.init();
				clone.go();
			} catch (CloneNotSupportedException e1) {
				e1.printStackTrace();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
			
			if(log.isDebugEnabled())
				log.debug("Sending message #"+(globalSeqNumber+1)+" with token = "+sendToken);

			Message m = ev.getMessage();			
			m.pushBoolean(sendToken);
			m.pushLong(++globalSeqNumber);
			try {
				ev.go();
				if(sendToken)
					rotateToken();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}			
		}
	}
	
	  private void storeUndelivered(SendableEvent ev, long seq) {
		  ev.getMessage().pushLong(seq);
		    ListIterator aux=undeliveredMessages.listIterator(undeliveredMessages.size());
		    while (aux.hasPrevious()) {
		      SendableEvent evaux=(SendableEvent)aux.previous();
		      long seqaux= evaux.getMessage().peekLong();
		      if (seqaux == seq) {
		        //debug("Received undelivered message already stored. Discarding new copy.");
		        return;
		      }
		      if (seqaux < seq) {
		        aux.next();
		        aux.add(ev);
		        return;
		      }
		    }
		    undeliveredMessages.addFirst(ev);
		  }


}
