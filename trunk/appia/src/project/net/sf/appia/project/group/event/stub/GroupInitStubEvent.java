package net.sf.appia.project.group.event.stub;

import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.Endpt;
import net.sf.appia.protocols.group.Group;

/**
 * GroupInitStubEvent is an event that clients send to
 * servers, saying that a new client has joined a group.
 * 
 * @author jtrindade
 */
public class GroupInitStubEvent extends StubEvent { 

	private Group group;
	private Endpt endpoint;
	
	public GroupInitStubEvent(){
		super();
	}
	
	public GroupInitStubEvent(Group group, Endpt endpoint){
		super();
		this.group = group;
		this.endpoint = endpoint;
	}
	
	public Endpt getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(Endpt endpoint) {
		this.endpoint = endpoint;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public void loadMessage() {
		Message message = this.getMessage();
		
		//Reconstruct the message
		this.setEndpoint((Endpt)message.popObject());
		this.setGroup((Group)message.popObject());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		message.pushObject(group);
		message.pushObject(endpoint);
		this.setMessage(message);
	}
}
