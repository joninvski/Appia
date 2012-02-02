package net.sf.appia.project.group.client;

import net.sf.appia.protocols.group.Endpt;

/**
 * This class simple associates a groupId with an endpoint.
 * It serves for a single VsStub session to manage various
 * groups.
 * 
 * @author jtrindade
 */
public class GroupData {

	private String groupId;
	private Endpt endpoint;
	
	protected GroupData(String groupId, Endpt endpoint){
		this.groupId = groupId;
		this.endpoint = endpoint;
	}
	
	public Endpt getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(Endpt endpoint) {
		this.endpoint = endpoint;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
