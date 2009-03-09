package net.sf.appia.project.group.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.sf.appia.project.group.GlobalConfigs;
import net.sf.appia.protocols.group.Endpt;
import net.sf.appia.protocols.group.Group;

/**
 * VsClient class represents a client connected to a group in the server.
 * 
 * @author jtrindade
 */
public class VsClient implements Serializable{

	private static final long serialVersionUID = 1143150895425826319L;

	private Endpt endpoint; //The endpoint for the client
	private Group group; //The group the client belongs to
	private InetSocketAddress clientAddress; //The address of the client
	private int pongTimer = 0;

	private boolean clientMute = false;

	//The address of the server to which the client is connected to
	private InetSocketAddress serverAttached; 

	protected VsClient(Endpt endpoint, Group group, InetSocketAddress clientAddress, 
			InetSocketAddress serverAttached){
		this.endpoint = endpoint;
		this.group = group;
		this.clientAddress = clientAddress;
		this.serverAttached = serverAttached;
	}

	public Endpt getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpt endpoint) {
		this.endpoint = endpoint;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public InetSocketAddress getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(InetSocketAddress clientAddress) {
		this.clientAddress = clientAddress;
	}

	public InetSocketAddress getServerAttached() {
		return serverAttached;
	}

	public void setServerAttached(InetSocketAddress serverAttached) {
		this.serverAttached = serverAttached;
	}

	public String toString(){
		return "Endpoint: " +  endpoint + " Address: " + clientAddress + " Group: " + group;
	}

	/**
	 * Asks if a server is attached to the client
	 * 
	 * @param serverAddress
	 * @return
	 */
	public boolean attachedTo(InetSocketAddress serverAddress){
		return this.serverAttached.equals(serverAddress);
	}

	private boolean attachedToNextPort(InetSocketAddress address) {
		int localport = this.serverAttached.getPort() + 1;

		if (address.getPort() == localport && 
				address.getHostName().equals(this.serverAttached.getHostName()))
			return true;

		return false;
	}

	/**
	 * 
	 * @param liveServerAddressess
	 * @return True if attached to any, False if attached to none
	 */
	public boolean attachedTo(SocketAddress[] liveServerAddressess) {
		for (SocketAddress address : liveServerAddressess){
			System.out.println("Comparing: " + address + " with " + serverAttached);

			if (this.attachedToNextPort((InetSocketAddress) address)){
				return true;
			}
		}
		return false;
	}

	public void resetPongTimer() {
		pongTimer = 0;
	}

	public void updatePong() {
		pongTimer++;
	}

	public boolean isThoughtDead() {
		return pongTimer > GlobalConfigs.PONG_NOT_RESPOND_MAX;
	}
	
	public boolean isClientMute() {
		return clientMute;
	}

	public void setClientMute(boolean clientMute) {
		this.clientMute = clientMute;
	}
}