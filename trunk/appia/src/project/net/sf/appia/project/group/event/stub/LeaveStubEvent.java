package net.sf.appia.project.group.event.stub;

import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.Endpt;

/**
 * The {@link LeaveStubEvent} is the event exchanged between the client and the
 * server, saying that a client wishes to leave a group.
 * 
 * @author jtrindade
 */
public class LeaveStubEvent extends StubEvent {
	private String groupId;
	private Endpt endpoint;
	
	public LeaveStubEvent(){
		super();
	}

	public LeaveStubEvent(String groupId, Endpt endpoint){
		super();
		setGroupId(groupId);
		setEndpoint(endpoint);
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Endpt getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpt endpoint) {
		this.endpoint = endpoint;
	}


	@Override
	public void loadMessage() {
		Message message = this.getMessage();

		//Reconstruct the message
		this.setGroupId(message.popString());
		this.setEndpoint((Endpt)message.popObject());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		message.pushObject(endpoint);
		message.pushString(groupId);
		this.setMessage(message);
	}
}   
