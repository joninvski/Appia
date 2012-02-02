package net.sf.appia.project.group.event.proxy;

import net.sf.appia.core.message.Message;
import net.sf.appia.project.group.server.VsGroup;
import net.sf.appia.protocols.group.Endpt;

public class UpdateProxyEvent extends ProxyEvent {

	private VsGroup[] allGroups;
	private Endpt serverThatSentEndpt;
	
	public UpdateProxyEvent(){
		super();
	}
	
	public UpdateProxyEvent(VsGroup[] allGroups, Endpt serverThatSendEndpt) {
		super();
		this.allGroups = allGroups;
		this.serverThatSentEndpt = serverThatSendEndpt;
	}

	@Override
	public void loadMessage() {
		Message message = this.getMessage();

		//Reconstruct the message
		this.setServerThatSentEndpt((Endpt)message.popObject());
		this.setAllGroups((VsGroup[])message.popObject());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		message.pushObject(allGroups);
		message.pushObject(serverThatSentEndpt);
		this.setMessage(message);
	}

	public VsGroup[] getAllGroups() {
		return allGroups;
	}

	public void setAllGroups(VsGroup[] allGroups) {
		this.allGroups = allGroups;
	}

	public Endpt getServerThatSentEndpt() {
		return serverThatSentEndpt;
	}

	public void setServerThatSentEndpt(Endpt serverThatSentEndpt) {
		this.serverThatSentEndpt = serverThatSentEndpt;
	}
}
