package net.sf.appia.project.group.event.proxy;

import net.sf.appia.core.message.Message;
import net.sf.appia.project.group.server.VsClient;
import net.sf.appia.protocols.group.Endpt;

/**
 * The NewClientProxyEvent event encapsulates a new client 
 * joining a group. This event is exchanged between the
 * various servers
 * 
 * @author jtrindade
 */
public class NewClientProxyEvent extends ProxyEvent {
	private VsClient client;

	public NewClientProxyEvent(){
		super();
	}

	public NewClientProxyEvent(VsClient vsClient, Endpt serverEndpoint){
		this.client = vsClient;
	}

	public VsClient getClient() {
		return client;
	}

	public void setClient(VsClient client) {
		this.client = client;
	}

	@Override
	public void loadMessage(){
		Message message = this.getMessage();

		//Reconstruct the message
		this.setClient((VsClient)message.popObject());
	}

	@Override
	public void storeMessage(){		
		Message message = this.getMessage();

		message.pushObject(client);
		this.setMessage(message);	
	}
}