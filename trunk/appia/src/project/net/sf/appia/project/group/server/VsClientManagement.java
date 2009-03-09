package net.sf.appia.project.group.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.sf.appia.protocols.group.Endpt;

/**
 * VsClientManagement provides several static methods to manage the
 * groups that clients create and join. This is a helper class to
 * simplify the code in the other classes.
 */
public class VsClientManagement {

	private static Dictionary<String, VsGroup> groupList = 
		new Hashtable<String, VsGroup>();

	/**
	 * Gets a VsGroup
	 * 
	 * @param groupid
	 * @return
	 */
	static protected VsGroup getVsGroup(String groupId) {
		synchronized(groupList){
			VsGroup clientsInGroup = groupList.get(groupId);

			//	If there is no group yet, create it!
			if (clientsInGroup == null){
				groupList.put(groupId, new VsGroup(groupId));
			}

			return groupList.get(groupId);
		}
	}

	/**
	 * Prints all the client attached to a group that this server
	 * knows of.
	 * 
	 * @param groupId
	 */
	static protected void printClients(String groupid){		
		synchronized(groupList){

			Enumeration<String> keys = groupList.keys();
			while (keys.hasMoreElements()){
				System.out.println(groupList.get(keys.nextElement()));
			}
		}
	}

	/**
	 * Removes a client from a group.
	 * 
	 * @param clientEndpoint
	 * @param groupId
	 */
	public static void removeClient(Endpt clientEndpoint, String groupId) {
		synchronized(groupList){
			VsGroup group = getVsGroup(groupId);

			group.removeClient(clientEndpoint);
		}
	}

	public static VsGroup[] getAllGroups() {
		synchronized(groupList){
			Enumeration<String> keys = groupList.keys();
			List<VsGroup> groups = new ArrayList<VsGroup>(); 

			while (keys.hasMoreElements()){
				VsGroup group = groupList.get(keys.nextElement());
				groups.add(group);
			}	
			return groups.toArray(new VsGroup[groups.size()]);
		}
	}


	public static void resetPongTimer(InetSocketAddress clientAddress, InetSocketAddress serverAddress) {
		synchronized(groupList){
			Enumeration<String> keys = groupList.keys();

			while (keys.hasMoreElements()){
				VsGroup group = groupList.get(keys.nextElement());

				VsClient client = group.getVsClient(clientAddress);

				//If the client is in the group, and attached to me
				if (client != null && client.attachedTo(serverAddress)){
					client.resetPongTimer();
				}
			}
		}
	}

	public static boolean checkIfAllInGroupAttachedToMeClientAreMute(String groupId, InetSocketAddress serverAddress) {
		synchronized(groupList){
			VsGroup group = getVsGroup(groupId);

			return group.areAllAttachedClientMute(serverAddress);
		}
	}

	public static void muteClient(String groupId, Endpt clientEndpt) {
		VsGroup group = groupList.get(groupId);
		VsClient client = group.getVsClient(clientEndpt);

		client.setClientMute(true);
	}

	public static void removeClient(List<VsClient> futureDeadClientEndpoints, String groupId) {
		synchronized(groupList){
			for(VsClient client : futureDeadClientEndpoints){
				System.out.println("FutureDeadClient: " + client);
				removeClient(client.getEndpoint(), groupId);
			}
		}
	}

	public static void addFutureDead(List<VsClient> futureDeadClients, String groupId) {
		for (VsClient dead : futureDeadClients){
			addFutureDead(dead, groupId);
		}
	}

	public static void addFutureDead(VsClient dead, String groupId) {
		synchronized(groupList){

			VsGroup group = getVsGroup(groupId);

			group.addFutureDead(dead);
		}
	}

	public static void replaceAGroup(VsGroup decidedGroup) {
		synchronized(groupList){
			String groupId = decidedGroup.getGroupId();

			//I must clean all pongs in the decidedGroup
			decidedGroup.cleanAllPongs();

			//Remove the old
			groupList.remove(groupId);


			//Add the new group
			groupList.put(groupId, decidedGroup);
		}
	}

	public static void unmuteAllClients(String decidedGroupId) {
		synchronized(groupList){

			VsGroup group = getVsGroup(decidedGroupId);

			group.unMuteAllClients();
		}
	}

	public static boolean hasSomeClientConnectedToServer(String groupId, InetSocketAddress serverAddress) {
		synchronized(groupList){

			VsGroup group = getVsGroup(groupId);

			return group.hasSomeClientConnectedToServer(groupId, serverAddress);
		}
	}

	public static void addAsFutureClient(String groupId, VsClient client) {
		synchronized(groupList){

			VsGroup group = getVsGroup(groupId);

			group.addAsFutureClient(client);
		}
	}



	public static List<VsGroup> getClientsNotAttachedTo(SocketAddress[] liveServerAddresses) {
		synchronized(groupList){

			List<VsGroup> groupOfClientsNotAttached = new ArrayList<VsGroup>();

			Enumeration<String> keys = groupList.keys();

			while (keys.hasMoreElements()){
				VsGroup group = groupList.get(keys.nextElement());
				List<VsClient> clients = group.getClientsNotAtachedTo(liveServerAddresses);

				VsGroup newGroup = new VsGroup(group.getGroupId(), clients);

				groupOfClientsNotAttached.add(newGroup);
			}

			return groupOfClientsNotAttached;
		}
	}

	public static void setNewAllGroups(VsGroup[] allGroups) {
		synchronized(groupList){

			for(VsGroup group : allGroups){
				group.cleanAllPongs();
				groupList = new Hashtable<String, VsGroup>();
				groupList.put(group.getGroupId(), group);
			}
		}
	}

	public static void printClients() {
		synchronized(groupList){

			Enumeration<String> keys = groupList.keys();

			while (keys.hasMoreElements()){
				System.out.println(groupList.get(keys.nextElement()));
			}
		}
	}

	/**
	 * Looks at the current view, and if a future dead client is already NOT present 
	 * in the view, it removes it from the future dead clients list 
	 * (because it is a present dead client :P)
	 * 
	 * @param groupId
	 */
	public static void updateFutureDeadClientsBasedOnCurrentView(String groupId) {
		synchronized(groupList){
			VsGroup group = getVsGroup(groupId);
			List<VsClient> futureDeadList = group.getFutureDead();
			List<VsClient> viewClientList = group.getClientsInGroup();
			List<VsClient> clientsToRemove = new ArrayList<VsClient>();

			for(VsClient futureDeadClient : futureDeadList){
				//If the future dead was already confirmed as dead in the view
				if(viewClientList.contains(futureDeadClient) == false){
					clientsToRemove.add(futureDeadClient);
				}
			}

			//Remove the clients already died in the view from our future dead list
			futureDeadList.removeAll(clientsToRemove);
		}
	}

	/**
	 * Looks at the current view, and if a future live client is already present 
	 * in the view, it removes it from the future live clients list 
	 * (because it is a present live client :P)
	 * 
	 * @param groupId
	 */
	public static void updateFutureLiveClientsBasedOnCurrentView(String groupId) {
		synchronized(groupList){

			VsGroup group = getVsGroup(groupId);
			List<VsClient> futureLiveClientList = group.getFutureLiveList();
			List<VsClient> viewClientList = group.getClientsInGroup();
			List<VsClient> clientsToAddToPresent = new ArrayList<VsClient>();

			for(VsClient futureDeadClient : futureLiveClientList){
				//If the client is already in the view i can remove it from the future list
				if(viewClientList.contains(futureDeadClient) == true){
					clientsToAddToPresent.add(futureDeadClient);
				}
			}

			//Remove the future clients that are already in the present view
			futureLiveClientList.addAll(clientsToAddToPresent);
		}
	}

	/**
	 * Removes the future deads present in the future dead list
	 * from the present active view.
	 * 
	 * @param groupId
	 */
	public static void setFutureDeadsIntoPresent(String groupId) {
		synchronized(groupList){

			VsGroup group = getVsGroup(groupId);
			List<VsClient> futureDeadList = group.getFutureDead();
			List<VsClient> viewClientList = group.getClientsInGroup();
			List<VsClient> clientsToRemoveFromPresent = new ArrayList<VsClient>();

			for(VsClient futureDeadClient : futureDeadList){
				//If the client is already in the view i can remove it from the future list
				if(viewClientList.contains(futureDeadClient) == true){
					futureDeadList.add(futureDeadClient);
				}
			}

			//Remove the future clients that are already in the present view
			viewClientList.removeAll(clientsToRemoveFromPresent);		
		}
	}

	/**
	 * Puts the future live clients present in the future live clients list
	 * into the present active view
	 * 
	 * @param groupId
	 */
	public static void setFutureClientsIntoPresent(String groupId) {
		synchronized(groupList){

			VsGroup group = getVsGroup(groupId);

			List<VsClient> viewClients = group.getClientsInGroup();
			List<VsClient> liveFutureClients = group.getFutureLiveList();
			List<VsClient> clientToPassToPresent = new ArrayList<VsClient>();

			//If the future client is not in the view, put it there
			for(VsClient futureClient :liveFutureClients){
				if(viewClients.contains(futureClient) == false){
					//Clean the client pong timer
					futureClient.resetPongTimer();
					clientToPassToPresent.add(futureClient);
				}
			}

			//I may now pass all the future clients into the present
			viewClients.addAll(clientToPassToPresent);
		}
	}

	/**
	 * Simply clears all clients present in the futureLiveClients list
	 * 
	 * @param groupId
	 */
	public static void clearFutureClients(String groupId) {
		synchronized(groupList){
			VsGroup group = getVsGroup(groupId);
			group.clearFutureClients();
		}
	}

	/**
	 * Simply clears the future live clients list
	 * 
	 * @param groupId
	 */
	public static void clearFutureDead(String groupId) {
		synchronized(groupList){
			VsGroup group = getVsGroup(groupId);
			group.clearFutureDeads();
		}		
	}

	public static List<VsClient> getAllClients() {
		List<VsClient> allClients = new ArrayList<VsClient>();

		VsGroup[] allGroups = getAllGroups();

		for(VsGroup group : allGroups){
			allClients.addAll(group.getClientsInGroup());
		}

		return allClients;
	}

	public static boolean checkIfEveryAllAttachedToMeClientAreMute(InetSocketAddress serverAddress) {
		synchronized(groupList){

			VsGroup[] allGroups = getAllGroups();

			for ( VsGroup group : allGroups){
				if(checkIfAllInGroupAttachedToMeClientAreMute(group.getGroupId(), serverAddress) == false){
					return false;
				}
			}
			return true;
		}
	}

	public static VsGroup[] getAllGroupsWithOnlyMyClients(InetSocketAddress myAddress) {	
		synchronized(groupList){
			List<VsGroup> groupsWithOnlyMyClient = new ArrayList<VsGroup>(); 
			
			VsGroup[] allGroups = getAllGroups();

			for (VsGroup group : allGroups){
				List<VsClient> groupClients = group.getClientsAttachedToServer(myAddress);
				VsGroup newGroup = new VsGroup(group.getGroupId(), groupClients);
				groupsWithOnlyMyClient.add(newGroup);
			}
			return groupsWithOnlyMyClient.toArray(new VsGroup[groupsWithOnlyMyClient.size()]);
		}
	}

	public static List<VsClient> getClientstAttachedTo(InetSocketAddress listenAddress) {
		VsGroup[] groupWithMyClients = getAllGroupsWithOnlyMyClients(listenAddress);

		List<VsClient> myClients = new ArrayList<VsClient>();
		
		for (VsGroup group : groupWithMyClients){
			List<VsClient> groupClients = group.getClientsAttachedToServer(listenAddress);
			myClients.addAll(groupClients);
		}
		return myClients;
	}
}
