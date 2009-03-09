package net.sf.appia.project.group.event.stub;

import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.Endpt;

public class BlockOkStubEvent extends StubEvent {

	private String groupId;
	private Endpt endpoint;
	
	public BlockOkStubEvent(){
		super();
	}

	public BlockOkStubEvent(String groupId, Endpt endpoint){
		super();
		this.groupId = groupId;
		this.endpoint = endpoint;
	}

	public Endpt getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpt endpoint) {
		this.endpoint = endpoint;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
