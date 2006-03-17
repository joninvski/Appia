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

public class UniformNode{
	
	private UniformHeader id; //id in order to recognize the message
	private int seen; //numer of nodes that seen this message
	private Event event;
	
	/**
	 * Basic Constructor.
	 * @param id the id of the message.
	 * @param e The message.
	 */
	public UniformNode(UniformHeader id,Event e){
		this.id = id;
		seen = 0;
		event = e;
	}
	
	/**
	 * Return the id of the messsage.
	 * @return the id of the messsage.
	 */
	public UniformHeader getId(){
		return id;
	}
	
	/**
	 * Return the number of nodes that have seen this message.
	 * @return the number of nodes that have seen this message.
	 */
	public int getSeen(){
		return seen;
	}
	
	/**
	 * @return The message.     
	 */
	public Event getEvent(){
		return event;
	}
	
	/**
	 * Set the message.
	 * @param e the message.
	 */
	public void setEvent(Event e){
		event = e;
	}
	
	
	/**
	 * Another node has seen this message.
	 * @return true if there is a majority.
	 */
	public boolean addSeen(int majority){
		seen++;
		
		if(seen >= majority)
			return true;
		else
			return false;
	}
	
	
	public int hashCode(){
		return id.hashCode();
	}
}
