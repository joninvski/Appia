package net.sf.appia.project.group.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.sf.appia.protocols.group.Endpt;

/**
 * VsGroup class represents a group to which clients connect to.
 * 
 * @author jtrindade
 */
public class VsGroup implements Serializable {

	private static final long serialVersionUID = -3441153614744803720L;

	private List<VsClient> clientsInGroup;
	private List<VsClient> futureDeadList;
	private List<VsClient> futureLiveList;
	private String groupId;

	private int currentVersion;

	protected VsGroup(String groupId){
		clientsInGroup = new ArrayList<VsClient>();
		futureDeadList = new ArrayList<VsClient>();
		futureLiveList = new ArrayList<VsClient>();
		this.groupId = groupId;
	}

	public VsGroup(String groupId, List<VsClient> clientsInGroup) {
		futureDeadList = new ArrayList<VsClient>();
		futureLiveList = new ArrayList<VsClient>();
		this.groupId = groupId;
		this.clientsInGroup = clientsInGroup;
	}

	public void addClient(VsClient client) {
		clientsInGroup.add(client);
	}

	public void addFutureDead(VsClient deadClient) {
		futureDeadList.add(deadClient);
	}

	public void cleanFutureDeadClients() {
		futureDeadList = new ArrayList<VsClient>();
	}

	public List<VsClient> getFutureDead() {
		return futureDeadList;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 
	 * @return The addresses for all clients present in the group
	 */
	public SocketAddress[] getAddresses() {
		ArrayList<SocketAddress> addresses = new ArrayList<SocketAddress>();
		for(VsClient client : clientsInGroup){
			addresses.add(client.getClientAddress());
		}
		return addresses.toArray(new SocketAddress[addresses.size()]);
	}

	/**
	 * 
	 * @return The endpoints for all clients present in the group
	 */
	public Endpt[] getEndpoints() {
		ArrayList<Endpt> endpoints = new ArrayList<Endpt>();
		for(VsClient client : clientsInGroup){
			endpoints.add(client.getEndpoint());
		}
		return endpoints.toArray(new Endpt[endpoints.size()]);
	}

	/**
	 * Gets all clients present in the group.
	 * @return
	 */
	public List<VsClient> getClientsInGroup() {
		return clientsInGroup;
	}

	/**
	 * Removes a client from the group
	 * 
	 * @param clientEndpoint
	 */
	public void removeClient(Endpt clientEndpoint) {
		for(VsClient client : clientsInGroup){
			if (client.getEndpoint().equals(clientEndpoint)){
				clientsInGroup.remove(client);
				return;
			}
		}
	}

	public String toString(){
		String str =  " Group: " + groupId + " Participants: " + clientsInGroup.size() + "\n";

		for (VsClient client : clientsInGroup){
			str += " " + client .getEndpoint() +  "Attached to: " + client.getServerAttached() + "\n";
		}

		return "\n" + str;
	}

	public VsClient getVsClient(InetSocketAddress clientAddress) {
		for (VsClient client : clientsInGroup){
			if (client.getClientAddress().equals(clientAddress)){
				return client;
			}
		}
		return null;
	}

	/**
	 * Returns a list with all dead clients in the group
	 *
	 * @return
	 */
	public List<VsClient> updatePongTimes(InetSocketAddress serverAddress) {
		List<VsClient> deadClients  = new ArrayList<VsClient>();

		for (VsClient client : clientsInGroup){
			client.updatePong();

			if(client.isThoughtDead() && client.attachedTo(serverAddress)){
				deadClients.add(client);
			}
		}

		return deadClients;
	}

	public boolean areAllAttachedClientMute(InetSocketAddress serverAddress){
		for (VsClient client : clientsInGroup) {
			if(client.attachedTo(serverAddress) && client.isClientMute() == false){
				return false;
			}
		}
		return true;
	}

	public void unMuteAllClients(){
		for (VsClient client : clientsInGroup) {
			client.setClientMute(false);
		}
	}

	public VsClient getVsClient(Endpt clientEndpt) {
		for (VsClient client : clientsInGroup){
			if (client.getEndpoint().equals(clientEndpt)){
				return client;
			}
		}
		return null;
	}

	public boolean hasSomeClientConnectedToServer(String groupId2, InetSocketAddress serverAddress) {
		for (VsClient client : clientsInGroup){
			if (client.attachedTo(serverAddress)){
				return true;
			}
		}
		return false;	
	}

	public void addAsFutureClient(VsClient futureClient) {
		if(futureClient == null){
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		futureLiveList.add(futureClient);
	}

	public List<VsClient> getClientsNotAtachedTo(SocketAddress[] liveServerAddresses) {
		List<VsClient> clientsToReturn = new ArrayList<VsClient>();

		//Discover what are the clients to remove
		for (VsClient client : clientsInGroup){
			if (client.attachedTo(liveServerAddresses) == false){
				clientsToReturn.add(client);
			}
		}
		return clientsToReturn;
	}

	public void mergeClients(VsGroup newGroup) {
		List<VsClient> newClients = newGroup.getClientsInGroup();

		for(VsClient client : newClients){
			//Only put the client, if he is not in there
			if(this.isClientInGroup(client) == false){
				System.out.println("Merging client: " + client);
				this.clientsInGroup.add(client);
			}
		}
	}

	private boolean isClientInGroup(VsClient client) {
		for(VsClient other : clientsInGroup){
			if(other.getEndpoint().equals(client.getEndpoint())){
				return true;
			}
		}
		return false;
	}

	public List<VsClient> getFutureLiveList() {
		return futureLiveList;
	}

	public void setFutureLiveList(List<VsClient> futureLiveList) {
		this.futureLiveList = futureLiveList;
	}

	//TODO - I don't like this
	public void insertFutureClientsIntoPresent() {
		clientsInGroup.addAll(futureLiveList);
		futureLiveList = new ArrayList<VsClient>();
	}

	public void clearFutureClients() {
		futureLiveList = new ArrayList<VsClient>();
	}

	public void clearFutureDeads() {
		futureDeadList = new ArrayList<VsClient>();
	}

	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public void incrementGroupViewVersion() {
		currentVersion++;
	}

	public void cleanAllPongs() {
		for(VsClient client : clientsInGroup){
			client.resetPongTimer();
		}		
	}

	public List<VsClient> getClientsAttachedToServer(InetSocketAddress myAddress) {
		List<VsClient> clientsAttachedToServer = new ArrayList<VsClient>();
		for (VsClient client : clientsInGroup) {
			if(client.getServerAttached().equals(myAddress)){
				clientsAttachedToServer.add(client);
				System.out.println("IS ATTACHED TO ME");
			}
		}
		return clientsAttachedToServer;
	}
	
	public static void printGroups(String text, VsGroup[] groups){
		System.out.print(text);
		for(VsGroup group : groups){
			System.out.println(group);
		}			
	}
}