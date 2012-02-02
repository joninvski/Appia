package net.sf.appia.project.group.event.proxy;

import net.sf.appia.core.message.Message;
import net.sf.appia.project.group.server.VsGroup;

public class UpdateDecideProxyEvent extends ProxyEvent {
	private VsGroup[] allGroups;
	
	public UpdateDecideProxyEvent(){
		super();
	}
	
	public UpdateDecideProxyEvent(VsGroup[] allGroups){
		super();
		this.allGroups = allGroups;
	}
	
	
	@Override
	public void loadMessage() {
		Message message = this.getMessage();

		//Reconstruct the message
		this.setAllGroups((VsGroup[])message.popObject());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();

		message.pushObject(allGroups);
		this.setMessage(message);	
	}

	public VsGroup[] getAllGroups() {
		return allGroups;
	}

	public void setAllGroups(VsGroup[] allGroups) {
		this.allGroups = allGroups;
	}
}
