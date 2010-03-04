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
package net.sf.appia.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

/*
 * Change Log: 
 * 
 */

/**
 * Title:        Appia<p>
 * Description:  Protocol development and composition framework<p>
 * Copyright:    Copyright (c) Alexandre Pinto & Hugo Miranda & Luis Rodrigues<p>
 * Company:      F.C.U.L.<p>
 * @author Alexandre Pinto & Hugo Miranda & Luis Rodrigues
 * @version 1.0
 */
public class EventScheduler {
	private Appia appia;

	//Canais que o escalonador controla
	Map<Channel, String> channels = new HashMap<Channel, String>();

	//Filas Up e Down de cada canal
	Map<String, Vector<Event>[]> upQueue = new HashMap<String, Vector<Event>[]>();
	Map<String, Vector<Event>[]> downQueue = new HashMap<String, Vector<Event>[]>();

	//Indicação de bloqueio das filas
	Map<String, Vector<Boolean>[]> blocked = new HashMap<String, Vector<Boolean>[]>();

	Vector<Event> assyncQueue = new Vector<Event>();

	private Vector<Event> currentSessionQueue = null;
	private Vector<Event>[] queueCurrent;
	private int currentSessionQueueIndex = 0;

	private int currentDirection=0; 
	private Channel currentChannel=null;
	private Session currentSession=null;

	private int removePosition = -1;

	public EventScheduler() {
		appia=Appia.appia;
		appia.instanceInsertEventScheduler(this);
	}

	public EventScheduler(Appia appia) {
		this.appia = appia;
		appia.instanceInsertEventScheduler(this);
	}


	public void insert(Event event) {

		if(!channels.containsKey(event.getChannel())){  //se o canal não está na lista de canais

			//adiciona o canal à lista 
			channels.put(event.getChannel(), event.getChannel().channelID);

			Vector<Event>[] updown = new Vector[event.getChannel().sessions.length+2];
			Vector<Boolean>[] block = new Vector[event.getChannel().sessions.length+2];

			int j = 0;
			while(j < (event.getChannel().sessions.length+2)){

				updown[j] = new Vector<Event>();
				block[j] = new Vector<Boolean>();
				j++;
			}

			//inicializa filas up e down e blocked
			upQueue.put(event.getChannel().channelID, updown);
			downQueue.put(event.getChannel().channelID, updown);
			blocked.put(event.getChannel().channelID, block);

			currentChannel = event.getChannel();
			queueCurrent = upQueue.get(event.getChannel().channelID);
			currentSessionQueue = upQueue.get(currentChannel.channelID)[0];
			currentDirection = Direction.UP;
		}


		if((Thread.currentThread() == appia.instanceGetAppiaThread()) &&
				(currentSession != null)){

			if(event.getDir() == Direction.DOWN)
			{
				Session[] sessionsList = event.getChannel().sessions;

				int index = 0;
				int i = 0;
				for(;i<sessionsList.length;i++){
					if(sessionsList[i].equals(currentSession)){
						index = i+1;
					}
				}

				Vector<Event>[] v = downQueue.get(event.getChannel().channelID);

				if(removePosition == 0)
					v[removePosition].add(event);
				else
				{
					v[removePosition-1].add(event);
					downQueue.put(event.getChannel().channelID, v);
				}
			}else{
				Session[] sessionsList = event.getChannel().sessions;

				int index = 0;
				int i = 0;
				for(;i<sessionsList.length;i++){
					if(sessionsList[i].equals(currentSession)){
						index = i+1;
					}
				}

				Vector<Event>[] v = upQueue.get(event.getChannel().channelID);

				if(removePosition == currentChannel.sessions.length)
					v[removePosition-1].add(event);
				else

				{
					v[removePosition+1].add(event);
					upQueue.put(event.getChannel().channelID, v);
				}

			}


		} else {
			synchronized (this) {
				assyncQueue.add(event);
			}
		}


		appia.instanceInsertedEvent();
	}

	public boolean consumeEvent() {

		boolean consumed = true;
		Event event = null;
		Session session;

		//se a fila actual tem eventos
		if(!isQueueEmpty(getCurrentQueue(currentChannel.channelID), currentSessionQueueIndex)){
			//processa o primeiro evento da fila
			event = removeElement(getCurrentQueue(currentChannel.channelID));       

		}else{ //se a fila actual não tem eventos processa a fila oposta ou a dos eventos assincronos

			//se a fila na dir oposta tem eventos 
			if(!isQueueEmpty(getNotCurrentQueue(currentChannel.channelID), 0)){

				//altera a fila actual para passar a ser a fila na dir oposta      
				queueCurrent = getNotCurrentQueue(currentChannel.channelID);
				currentDirection = Direction.invert(currentDirection);

				if(currentDirection == Direction.DOWN){
					currentSessionQueueIndex = 	currentChannel.sessions.length+1; //max indice do vector
					currentSessionQueue = queueCurrent[currentChannel.sessions.length+1];	
				}else{
					currentSessionQueueIndex = 0;
					currentSessionQueue = queueCurrent[0];
				}

				//processa um elemento da fila
				boolean hasEvent = false;

				while(!hasEvent){

					if(currentSessionQueue.size()==0){
						currentSessionQueue = queueCurrent[currentSessionQueueIndex+currentDirection];
						currentSessionQueueIndex = currentSessionQueueIndex+currentDirection;
					} else
						hasEvent = true;

				}

				event = removeElement(getCurrentQueue(currentChannel.channelID));       

			}else{   //se nenhuma fila tem eventos processa os assincronos

				synchronized (this) {

					//se a fila de eventos assincronos tem eventos
					if(assyncQueue.size() > 0){
						event = assyncQueue.remove(0);

						if(event.getDir() == Direction.UP)
							removePosition = 0;
						else
							removePosition = event.getChannel().sessions.length;

					}else{   //não há eventos para consumir
						consumed = false;
					}
				}
			}

		}

		//se a fila actual ficou vazia actualiza a fila actual

		if(isQueueEmpty(getCurrentQueue(currentChannel.channelID), currentSessionQueueIndex)){

			currentSessionQueueIndex += currentDirection;

			if((currentSessionQueueIndex < currentChannel.sessions.length+2) && (currentSessionQueueIndex >=0) && !isQueueEmpty(queueCurrent, currentSessionQueueIndex)){

				currentSessionQueue = queueCurrent[currentSessionQueueIndex];


			}else{//troca a fila para a fila oposta

				if(currentDirection == Direction.UP){
					currentSessionQueue = getNotCurrentQueue(currentChannel.channelID)[currentChannel.sessions.length+1];
					currentSessionQueueIndex = currentChannel.sessions.length+1;

					currentDirection = Direction.DOWN;
					queueCurrent = downQueue.get(currentChannel.channelID);
				}
				else{ //direcção == DOWN
					//troca para o outro canal
					Set<Entry<Channel, String>> allchannels = channels.entrySet();
					Iterator it = allchannels.iterator();

					Entry<Channel, String> channel = (Entry<Channel, String>) it.next();

					while(channel.getValue() != currentChannel.channelID && it.hasNext()){
						channel = (Entry<Channel, String>) it.next();
					}

					if(it.hasNext())
						channel = (Entry<Channel, String>) it.next();


					currentChannel = channel.getKey();
					queueCurrent = upQueue.get(currentChannel.channelID);
					currentSessionQueue = upQueue.get(currentChannel.channelID)[0];
					currentSessionQueueIndex = 0;
					currentDirection = Direction.UP;

				}

			}
		}



		if (consumed) {

			session=event.popSession();

			if (session != null) {
				currentSession=session;
				currentDirection=event.getDir();
				currentChannel=event.getChannel();

				//entrega o evento à sessão, para já entrega a todas
				try {
					session.handle(event);
				} catch (RuntimeException e) {
					e.printStackTrace();
					System.err.println("--------------------------------"+
							"Exception report:"+
							"\nSession: "+session+
							"\nEvent: "+event+
							"\nDirection: "+((event.getDir()==Direction.UP)?"UP":"DOWN")+
							"\nSourceSession: "+event.getSourceSession()+
							"\nChannel: "+event.getChannel()+
					"\n--------------------------------");
					throw e;
				}
				currentSession=null;
			}
		}

		return consumed;

	} 



	public Session getHandelingSession() {
		return currentSession;
	}

	public Appia getAppiaInstance() {
		return appia;
	}

	public void start() {}
	public void stop() {}


	private Event removeElement(Vector<Event>[] vectors) {

		boolean hasEvent = false;

		while(!hasEvent){

			if(currentDirection==Direction.UP){

				if(vectors[currentSessionQueueIndex].isEmpty()){
					currentSessionQueueIndex++;
				}
				else
					hasEvent = true;
			}else{
				if(vectors[currentSessionQueueIndex].isEmpty()){
					currentSessionQueueIndex--;
				}
				else
					hasEvent = true;

			}
		}

		removePosition = currentSessionQueueIndex;

	
			return vectors[currentSessionQueueIndex].remove(0);
			

	}

	private boolean isQueueEmpty(Vector<Event>[] a, int index){

		if(a == null){
			System.out.println("a NULL");
			return true;
		}

		int count = 0;


		if(currentDirection == Direction.UP){
			for(int i = index; i<a.length; i++){
				count +=  a[i].size();
			}
		}else{
			for(int i = index; i>=0; i--){
				count +=  a[i].size();
			}
		}

		if(count > 0)
			return false;
		else
			return true;

	}


	private Vector<Event>[] getCurrentQueue(String c) {

		if(queueCurrent == (downQueue.get(c)))
			return downQueue.get(c);
		else
			return upQueue.get(c);
	}

	private Vector<Event>[] getNotCurrentQueue(String c) {


		if(queueCurrent == (downQueue.get(c))){
			return upQueue.get(c);}
		else{
			return downQueue.get(c);}
	}

}

