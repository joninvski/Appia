package net.sf.appia.project.group.server;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import net.sf.appia.protocols.group.Endpt;

public class UpdateManager {

	private static List<Endpt> updatesReceived = new ArrayList<Endpt>();
	private static Dictionary<String, VsGroup> temporaryView = new Hashtable<String, VsGroup>();

	public static void addUpdateMessageReceived(Endpt servertThatSentEndpt, VsGroup[] newGroups) {
		synchronized (updatesReceived) {
			updatesReceived.add(servertThatSentEndpt);

			for (VsGroup newGroup : newGroups) {
				VsGroup existingGroup = getTemporaryView(newGroup.getGroupId());

				existingGroup.mergeClients(newGroup);
			}
		}
	}

	private static VsGroup getTemporaryView(String groupId) {
		VsGroup group = temporaryView.get(groupId);

		if(group == null){
			group = new VsGroup(groupId);
			temporaryView.put(groupId, group);
		}
		return group;
	}

	public static boolean receivedUpdatesFromAllLiveServers(Endpt[] allLiveServerEndpts) {
		for(Endpt liveEndpt : allLiveServerEndpts){
			if(updatesReceived.contains(liveEndpt) == false){
				return false;				
			}
		}
		return true;
	}

	public static VsGroup[] getTemporaryUpdateList() {
		Enumeration<String> keys = temporaryView.keys();
		List<VsGroup> toReturn = new ArrayList<VsGroup>();

		while(keys.hasMoreElements()){
			toReturn.add(temporaryView.get(keys.nextElement()));
		}

		return toReturn.toArray(new VsGroup[toReturn.size()]);
	}

	public static void cleanUpdateViews() {
		temporaryView = new Hashtable<String, VsGroup>();
		updatesReceived = new ArrayList<Endpt>();
	}
	
	public static void printTemporaryView(){
		for (VsGroup group : getTemporaryUpdateList()){
			System.out.println("Temporary: "+  group);
		}
	}
}
