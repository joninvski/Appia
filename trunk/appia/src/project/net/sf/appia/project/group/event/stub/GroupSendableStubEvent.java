package net.sf.appia.project.group.event.stub;

import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.events.GroupSendableEvent;

/**
 * The {@link GroupInitStubEvent} is a stub event that clients 
 * seend to servers, contained an encapsulated groupSendableEvent
 * 
 * @author jtrindade
 */
public class GroupSendableStubEvent extends StubEvent {
	private String groupId;
	private GroupSendableEvent encapsulatedEvent;
	
	public GroupSendableStubEvent(){
		super();
	}

	public GroupSendableStubEvent(String groupId, 
			GroupSendableEvent encapsulatedEvent){
		super();
		setGroupId(groupId);
		setEncapsulatedEvent(encapsulatedEvent);
	}

	public GroupSendableEvent getEncapsulatedEvent() {
		return encapsulatedEvent;
	}

	public void setEncapsulatedEvent(GroupSendableEvent encapsulatedEvent) {
		this.encapsulatedEvent = encapsulatedEvent;
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
		this.setEncapsulatedEvent((GroupSendableEvent) message.popObject());
		this.setGroupId(message.popString());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		message.pushString(groupId);
		message.pushObject(encapsulatedEvent);
		this.setMessage(message);
	}
}   