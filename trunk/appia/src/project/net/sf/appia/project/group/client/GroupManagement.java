package net.sf.appia.project.group.client;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * This classes manages the various groups that a 
 * VsStubSession possesses. It is a simple interface to 
 * to a Dictionary containing groupDatas.
 *
 * @author jtrindade
 */
public class GroupManagement {
	static private Dictionary<String, GroupData> groups = 
		new Hashtable<String, GroupData>();
	
	protected static void AddGroup(GroupData groupData){
		groups.put(groupData.getGroupId(), groupData);
	}
	
	protected static GroupData getGroupData(String groupId){
		return groups.get(groupId);
	}

	public static void removeGroup(String id) {
		GroupData group = groups.get(id);
		
		groups.remove(group);
	}
}