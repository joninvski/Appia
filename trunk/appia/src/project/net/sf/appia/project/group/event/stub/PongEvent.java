package net.sf.appia.project.group.event.stub;

import java.net.InetSocketAddress;

import net.sf.appia.core.message.Message;

public class PongEvent extends StubEvent {
	
	private InetSocketAddress clientAddress;

	public PongEvent(){
		super();
	}
	
	public PongEvent(InetSocketAddress clientAddress)
	{
		this.clientAddress = clientAddress;
	}

	@Override
	public void loadMessage() {
		Message message = this.getMessage();

		//Reconstruct the message
		this.setClientAddress((InetSocketAddress)message.popObject());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		message.pushObject(clientAddress);
		this.setMessage(message);
	}

	public InetSocketAddress getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(InetSocketAddress clientAddress) {
		this.clientAddress = clientAddress;
	}
}
