package net.sf.appia.project.group.event.proxy;

import java.util.ArrayList;
import java.util.List;

import net.sf.appia.core.message.Message;
import net.sf.appia.project.group.server.VsClient;

/**
 * LeaveClientProxyEvent represents a client that whish to
 * leave a group. This message is exchanged between servers.
 * 
 * @author jtrindade
 */
public class LeaveClientProxyEvent extends ProxyEvent {
	private List<VsClient> futureDeadClients;
	private String groupId;
	
	public LeaveClientProxyEvent(){
		super();
	}
	
	public LeaveClientProxyEvent(List<VsClient> futureDeadClientEndpoints, String groupId){
		super();
		this.futureDeadClients = futureDeadClientEndpoints;
		this.groupId = groupId;
	}
	
	public LeaveClientProxyEvent(VsClient futureDeadClient, String groupId){
		super();
		futureDeadClients = new ArrayList<VsClient>();	
		this.futureDeadClients.add(futureDeadClient);
		this.groupId = groupId;
	}
	
	
	public List<VsClient> getFutureDeadClients() {
		return futureDeadClients;
	}

	public void setFutureDeadClients(List<VsClient> futureDeadClient) {
		this.futureDeadClients = futureDeadClient;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadMessage(){
		Message message = this.getMessage();

		//Reconstruct the message
		this.setFutureDeadClients((List<VsClient>)message.popObject());
		this.setGroupId(message.popString());

	}
	
	@Override
	public void storeMessage(){
		Message message = this.getMessage();
		
		message.pushString(groupId);
		message.pushObject(futureDeadClients);	
		this.setMessage(message);	
	}
}