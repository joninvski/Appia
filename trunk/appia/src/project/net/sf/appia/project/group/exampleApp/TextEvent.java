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
 /*
 * Created on Mar 15, 2004
 */
package net.sf.appia.project.group.exampleApp;

import java.io.Serializable;

import net.sf.appia.core.*;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.Group;
import net.sf.appia.protocols.group.events.GroupSendableEvent;

/**
 * Event that takes care of text messages.
 * 
 * @author  Liliana Rosa & Nuno Almeida
 */
public class TextEvent extends GroupSendableEvent implements Serializable {
	
	private static final long serialVersionUID = -152408707037970905L;
	private String username;
	private String message;
	private int share;
	
	public TextEvent() {
		super();
	}
	
	public TextEvent(Channel channel, int dir, Session source,
			Group group) throws AppiaEventException {
		super(channel,dir,source,group,null);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String user) {
		username = user;
	}
	
	public String getUserMessage() {
		return message;
	}
	
	public void setUserMessage(String msg) {
		message = msg;
	}
	
	public int getShare() {
		return share;
	}
	
	public void setShare(int i) {
		share = i;
	}
	 
	public void loadMessage(){
		Message message = this.getMessage();

		//Reconstruct the message
		this.setUserMessage(message.popString());
		this.setUsername(message.popString());
	}
	
	public void storeMessage(){
		Message message = this.getMessage();
		message.pushString(this.getUsername());
		message.pushString(this.getUserMessage());
		this.setMessage(message);	
	}
}