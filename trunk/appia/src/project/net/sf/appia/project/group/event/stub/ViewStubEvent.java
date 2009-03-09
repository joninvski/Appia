package net.sf.appia.project.group.event.stub;

import java.net.SocketAddress;

import net.sf.appia.core.message.Message;
import net.sf.appia.project.group.server.VsGroup;
import net.sf.appia.protocols.group.AppiaGroupException;
import net.sf.appia.protocols.group.Endpt;
import net.sf.appia.protocols.group.Group;
import net.sf.appia.protocols.group.LocalState;
import net.sf.appia.protocols.group.ViewID;
import net.sf.appia.protocols.group.ViewState;
import net.sf.appia.protocols.group.intra.View;

/**
 * The {@link ViewStubEvent} is a stub event with info that allows 
 * the construction of a View. This is echanged between the server and 
 * the client.
 *
 * @author jtrindade
 */
public class ViewStubEvent extends StubEvent {
	private VsGroup vsGroup;

	public ViewStubEvent(){
		super();
	}

	public ViewStubEvent(VsGroup group){
		super();
		setGroup(group);
	}

	public VsGroup getGroup() {
		return vsGroup;
	}

	public void setGroup(VsGroup group) {
		this.vsGroup = group;
	}

	@Override
	public void loadMessage() {
		Message message = this.getMessage();

		//Reconstruct the message
		this.setGroup((VsGroup)message.popObject());
	}

	@Override
	public void storeMessage() {
		Message message = this.getMessage();
		message.pushObject(vsGroup);
		this.setMessage(message);
	}


	public View createView(Endpt myEndpoint){
		//Necessary for the View State
		Group group = new Group(vsGroup.getGroupId());

		ViewID vId = new ViewID(0,myEndpoint);
		ViewID[] oldViewIds = {vId};
		Endpt[] endpoints = getEndpoints();
		SocketAddress[] addresses = getAddresses();

		//Construct the View State
		ViewState vs;
		try {
			vs = new ViewState("1", group, vId, oldViewIds, endpoints, addresses);

			//Now for the LocalState
			LocalState ls = new LocalState(vs, myEndpoint);

			//Finally construct the view
			View realView = new View(vs, ls);
			return realView;
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		} catch (AppiaGroupException e) {
			e.printStackTrace();
			return null;
		}
	}

	private SocketAddress[] getAddresses() {
		return vsGroup.getAddresses();
	}

	private Endpt[] getEndpoints() {
		return vsGroup.getEndpoints();
	}
}   
