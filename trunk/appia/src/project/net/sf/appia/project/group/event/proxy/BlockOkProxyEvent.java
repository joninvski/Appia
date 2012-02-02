package net.sf.appia.project.group.event.proxy;

import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.Endpt;

public class BlockOkProxyEvent extends ProxyEvent {
	int viewVersion = 0;
	
	public int getViewVersion() {
		return viewVersion;
	}

	public void setViewVersion(int viewVersion) {
		this.viewVersion = viewVersion;
	}

	public String groupId;
	public Endpt serverThatShutUpEndpt;

	public BlockOkProxyEvent(){
		super();
	}

	public BlockOkProxyEvent(String groupId, Endpt serverThatShutUpEndpt, int version){
		super();
		this.groupId = groupId;
		this.serverThatShutUpEndpt = serverThatShutUpEndpt;
		this.setViewVersion(version);
	}

	@Override
	public void loadMessage() {
		//Reconstruct the message
		this.setServerThatShutUpEndpt((Endpt)message.popObject());
		this.setGroupId(message.popString());
		this.setViewVersion(message.popInt());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();

		message.pushInt(getViewVersion());
		message.pushString(groupId);
		message.pushObject(serverThatShutUpEndpt);	
		this.setMessage(message);	
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Endpt getServerThatShutUpEndpt() {
		return serverThatShutUpEndpt;
	}

	public void setServerThatShutUpEndpt(Endpt serverThatShutUpEndpt) {
		this.serverThatShutUpEndpt = serverThatShutUpEndpt;
	}

}
