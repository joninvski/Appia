package net.sf.appia.project.group.event.proxy;

import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.events.GroupSendableEvent;

/**
 * GroupSendableProxyEvent is exchanged between servers and 
 * encapsulates groupsendable events that clients which to
 * send to all members of a group.
 * 
 * @author jtrindade
 */
public class GroupSendableProxyEvent extends ProxyEvent {
	private String groupId;
	private GroupSendableEvent sendableEvent;
	
	public GroupSendableProxyEvent(){
		super();
	}
	
	public GroupSendableProxyEvent(String groupId, GroupSendableEvent sendableEvent){
		super();
		this.groupId = groupId;
		this.sendableEvent = sendableEvent;
	}
	
	public GroupSendableEvent getSendableEvent() {
		return sendableEvent;
	}

	public void setSendableEvent(GroupSendableEvent sendableEvent) {
		this.sendableEvent = sendableEvent;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public void loadMessage(){
		Message message = this.getMessage();

		//Reconstruct the message
		this.setGroupId(message.popString());
		this.setSendableEvent((GroupSendableEvent)message.popObject());
	}
	
	@Override
	public void storeMessage(){
		Message message = this.getMessage();
		message.pushObject(sendableEvent);
		message.pushString(groupId);
		this.setMessage(message);	
	}
}