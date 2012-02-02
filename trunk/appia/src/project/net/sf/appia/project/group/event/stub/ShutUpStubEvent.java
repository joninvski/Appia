package net.sf.appia.project.group.event.stub;

import net.sf.appia.core.message.Message;

public class ShutUpStubEvent extends StubEvent {

	private String groupId;
	
	public ShutUpStubEvent(){
		super();
	}
	
	public ShutUpStubEvent(String groupId){
		super();
		this.setGroupId(groupId);
	}
	
	@Override
	public void loadMessage() {
		Message message = this.getMessage();

		//Reconstruct the message
		this.setGroupId(message.popString());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		message.pushString(groupId);
		this.setMessage(message);
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
