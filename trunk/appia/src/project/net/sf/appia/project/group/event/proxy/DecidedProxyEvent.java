package net.sf.appia.project.group.event.proxy;

import net.sf.appia.core.message.Message;
import net.sf.appia.project.group.server.VsGroup;

public class DecidedProxyEvent extends ProxyEvent {

	VsGroup decidedVsGroup;

	public DecidedProxyEvent(){
		super();
	}

	public DecidedProxyEvent(VsGroup decidedVsGroup){
		super();
		this.decidedVsGroup = decidedVsGroup;
	}

	@Override
	public void loadMessage() {
		Message message = this.getMessage();

		//Reconstruct the message
		this.setDecidedVsGroup((VsGroup)message.popObject());	
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		
		message.pushObject(decidedVsGroup);
		this.setMessage(message);
	}

	public VsGroup getDecidedVsGroup() {
		return decidedVsGroup;
	}

	public void setDecidedVsGroup(VsGroup decidedView) {
		this.decidedVsGroup = decidedView;
	}

}
