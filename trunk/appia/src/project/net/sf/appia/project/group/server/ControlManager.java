package net.sf.appia.project.group.server;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import net.sf.appia.protocols.group.Endpt;

public class ControlManager {

	private static Dictionary<String, ReceivedConfirmations> controlsReceived = new Hashtable<String, ReceivedConfirmations>();

	public static boolean receivedFromAllLiveServers(String groupId, Endpt[] allLiveServerEndpts) {
		synchronized (controlsReceived) {
			ReceivedConfirmations receivedConfirmations = getControlsReceived(groupId);

			for(Endpt endpt : allLiveServerEndpts){
				if(receivedConfirmations.containsEndpt(endpt) == false)
					return false;
			}

			return true;
		}
	}

	public static void addControlMessageReceived(String groupId, Endpt serverThatSendEndpt) {
		synchronized (controlsReceived) {
			ReceivedConfirmations receivedConfirmations = getControlsReceived(groupId);
			receivedConfirmations.addConfirmation(serverThatSendEndpt);
		}
	}


	private static ReceivedConfirmations getControlsReceived(String groupId) {

		synchronized (controlsReceived) {
			ReceivedConfirmations receivedConfirmations = controlsReceived.get(groupId);

			//If it is the first that I have received for this version
			if(receivedConfirmations == null){
				controlsReceived.put(groupId, new ReceivedConfirmations());
				//Let's get it again 
				receivedConfirmations = controlsReceived.get(groupId);
			}

			return receivedConfirmations;
		}
	}

	public static void printControl(String groupId) {
		synchronized (controlsReceived) {
			ReceivedConfirmations receivedConfirmations = controlsReceived.get(groupId);
			System.out.println("Confirmations for : " + groupId);

			for(Endpt endpoint : receivedConfirmations.confirmations){
				System.out.println(endpoint);
			}
		}
	}
	
	public static void removeControl(String decidedGroupId) {
		synchronized (controlsReceived) {
			controlsReceived.remove(decidedGroupId);
		}
	}

	/**
	 * This is nothing more than a list of the addresses that have sent me the messages
	 */
	public static class ReceivedConfirmations{
		private List<Endpt> confirmations = new ArrayList<Endpt>();

		public void addConfirmation(Endpt serverThatConfirm) {
			confirmations.add(serverThatConfirm);
		}

		public boolean containsEndpt(Endpt serverEndpt) {
			return confirmations.contains(serverEndpt);
		}
	}
}
