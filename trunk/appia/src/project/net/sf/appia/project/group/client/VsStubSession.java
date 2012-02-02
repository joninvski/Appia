/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2006 University of Lisbon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Alexandre Pinto and Hugo Miranda.
 * Contributor(s): See Appia web page for a list of contributors.
 */
package net.sf.appia.project.group.client;

import java.net.InetSocketAddress;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.AppiaException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.EchoEvent;
import net.sf.appia.project.group.event.stub.BlockOkStubEvent;
import net.sf.appia.project.group.event.stub.GroupInitStubEvent;
import net.sf.appia.project.group.event.stub.GroupSendableStubEvent;
import net.sf.appia.project.group.event.stub.LeaveStubEvent;
import net.sf.appia.project.group.event.stub.PongEvent;
import net.sf.appia.project.group.event.stub.ShutUpStubEvent;
import net.sf.appia.project.group.event.stub.StubEvent;
import net.sf.appia.project.group.event.stub.ViewStubEvent;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import net.sf.appia.protocols.group.Endpt;
import net.sf.appia.protocols.group.Group;
import net.sf.appia.protocols.group.ViewID;
import net.sf.appia.protocols.group.events.GroupInit;
import net.sf.appia.protocols.group.events.GroupSendableEvent;
import net.sf.appia.protocols.group.intra.View;
import net.sf.appia.protocols.group.leave.LeaveEvent;
import net.sf.appia.protocols.group.sync.BlockOk;
import net.sf.appia.xml.interfaces.InitializableSession;
import net.sf.appia.xml.utils.SessionProperties;


/**
 * This class defines a Layer that counts the messages that go up and down.
 * 
 * @author Joao Trindade
 * @version 1.0
 */
public class VsStubSession extends Session implements InitializableSession {

	Channel channel; //The channel to communicate with the server
	InetSocketAddress serverAddress; //The server adddress to send the messanges
	private InetSocketAddress localAddress;

	/**
	 * Creates a new EccoSession.
	 * @param l
	 */
	public VsStubSession(VsStubLayer l) {
		super(l);
	}

	/*
	 * Inits the paremeters for this session
	 * 
	 * @see net.sf.appia.xml.interfaces.InitializableSession#init(net.sf.appia.xml.utils.SessionProperties)
	 */
	public void init(SessionProperties params) {
		String serverHost = params.getProperty("serverhost");
		int serverPort = Integer.parseInt(params.getProperty("serverport"));

		serverAddress = new InetSocketAddress(serverHost, serverPort);
	}


	/**
	 * Main event handler.
	 * @param ev the event to handle.
	 * 
	 * @see net.sf.appia.core.Session#handle(net.sf.appia.core.Event)
	 */
	public void handle(Event ev) {
//		System.out.println("VsStub session - "+ ev.getDir() 
//				+" - Received an event type - " + ev.getClass());

		try {
			/********** Normal Events **********/
			if (ev instanceof RegisterSocketEvent)
				handleRegisterSocketEvent((RegisterSocketEvent) ev);
			else if (ev instanceof GroupInit)
				handleGroupInit((GroupInit) ev);
		
			else if (ev instanceof BlockOk)
				handleBlockOk((BlockOk) ev);

			/********** Vs Events - Are encapsulated **********/
			else if (ev instanceof ViewStubEvent)
				handleViewStubEvent((ViewStubEvent) ev);
			else if (ev instanceof LeaveEvent)
				handleLeaveEvent((LeaveEvent) ev);
			else if (ev instanceof GroupSendableEvent)
				handleGroupSendableEvent((GroupSendableEvent) ev);

			/********** Stub Events - Are desencapsulated **********/
			else if (ev instanceof GroupSendableStubEvent)
				handleGroupSendableStubEvent((GroupSendableStubEvent) ev);
			else if (ev instanceof ShutUpStubEvent)
				handleShutUpStubEvent((ShutUpStubEvent) ev);

			/********** Pong detecture - For failure detection ********/
			else if (ev instanceof PongEvent)
				handlePongEvent((PongEvent) ev);
			
			/********* If we don't handle the event warn *******/
			else{
				System.out.println("VsStubSession: Event not treated - " + 
						ev.getClass().toString());
				ev.go();
			}
		}
		catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}


	private void handleBlockOk(BlockOk ok) throws AppiaEventException {
		//Let's get the arguments
		String groupId = ok.group.id;
		GroupData groupData = GroupManagement.getGroupData(groupId);
	
		//Create the blockOkStubEvent
		BlockOkStubEvent stub = new BlockOkStubEvent(groupId, groupData.getEndpoint());
		
		//Send the Stub Event to the server
		sendStubEvent(stub);
	}

	/*********** START - Events that need to be encapsulated ************
	 * @throws AppiaEventException */

	private void handleShutUpStubEvent(ShutUpStubEvent event) throws AppiaEventException {
		event.loadMessage();
		
		//We'll get the necessary arguments
		String groupId = event.getGroupId();
		GroupData groupData = GroupManagement.getGroupData(groupId);
		ViewID viewId = new ViewID(0, groupData.getEndpoint());
		Group group = new Group(groupId);

		//Construct the blockOK event
		BlockOk blockOk = new BlockOk(event.getChannel(), Direction.DOWN, this, group, viewId);
		
		//Let's send a blockOK 
		EchoEvent echo = new EchoEvent(blockOk, event.getChannel(), Direction.UP, this);
		echo.init();
        echo.go();
	}

	/**
	 * Handles the group sendable events sent by the upper session. We must encapsulate
	 * it and then send it to the server.
	 * @param event
	 * @throws AppiaEventException
	 */
	private void handleGroupSendableEvent(GroupSendableEvent event) throws AppiaEventException {
		//Get the necessary parameters
		String groupId = event.group.id;

		//Create the stub event
		GroupSendableStubEvent stub = new GroupSendableStubEvent(groupId, event);

		//Send the stub to the server
		sendStubEvent(stub);
	}

	/**
	 * Handles the leave event sent from the upper layer. We must encapsulate it and
	 * send to the servers.
	 * 
	 * @param event
	 * @throws AppiaEventException
	 */
	private void handleLeaveEvent(LeaveEvent event) throws AppiaEventException {
		//Get the necessary parameters
		Group group = event.group;
		GroupData groupData = GroupManagement.getGroupData(group.id);

		//Remove the group
		GroupManagement.removeGroup(group.id);
		
		//Create the stub event
		LeaveStubEvent leaveStub = new LeaveStubEvent(group.id, groupData.getEndpoint());

		//Send the stub to the server
		sendStubEvent(leaveStub);
	}	
	
	/**
	 * Handles the group Init events that arrive to the client
	 * @param groupInit
	 * @throws AppiaEventException
	 */
	private void handleGroupInit(GroupInit groupInit) throws AppiaEventException {
		if(groupInit.getDir() == Direction.DOWN){
			// Get group Init main parameters
			Group group = groupInit.getVS().group;
			Endpt endpoint = groupInit.getEndpt();
			
			// Create the stub
			StubEvent stubEvent = new GroupInitStubEvent(group, endpoint);

			//Store the Group related info
			GroupData groupData = new GroupData(group.id, endpoint);
			GroupManagement.AddGroup(groupData);

			// Send the stub to the server
			sendStubEvent(stubEvent);
		}
	}
	/*********** END - Events that need to be encapsulated *************/


	/******** START - Events originated from the server *******************/
	/********         that need to be desencapsulated   *******************/
	
	/**
	 * Handles the reception of encapsulated views. These are then desencapsulated 
	 * and sent to the upper layer
	 * 
	 * @param event
	 * @throws AppiaEventException
	 */
	private void handleViewStubEvent(ViewStubEvent event) throws AppiaEventException {
		event.loadMessage();

		GroupData groupData = GroupManagement.getGroupData(event.getGroup().getGroupId());

		//We must construct the correct view
		View view = event.createView(groupData.getEndpoint());

		//And now send it upward
		SendUpward(view);
	}
	/**
	 * Handles the group sendable events that were encapsulated. These encapsulated group 
	 * sendable events are what was sent from the other servers.
	 * 
	 * @param event
	 * @throws AppiaEventException
	 */
	
	private void handleGroupSendableStubEvent(GroupSendableStubEvent event) throws AppiaEventException {
		//If we receive the stub event going up
		if(event.getDir() == Direction.UP){
			event.loadMessage();

			//I have to get the original
			GroupSendableEvent groupEvent = event.getEncapsulatedEvent();

			//And now we sent the event upward
			SendUpward(groupEvent);
		}
		
		//If it is going down, we let it continue
		else{
			event.go();
		}
	}
	/******** END - Events originated from the server *******************/
	/********         that need to be desencapsulated   *******************/


	/**
	 * Handles the register socket event. This will be our channel to
	 * communicate with the server
	 * 
	 * @param event
	 * @throws AppiaEventException
	 */
	private void handleRegisterSocketEvent(RegisterSocketEvent event) throws AppiaEventException {
		if(event.getDir() == Direction.UP && event.error == false){
			channel = event.getChannel();
			try {
				localAddress = (InetSocketAddress) event.getLocalSocketAddress();
				launchPongThread(channel);
			} catch (AppiaException e) {
				System.exit(1);
				return;
			}
		}

		event.go();
	}

	private void launchPongThread(Channel clientChannel) {
		//Launch the Pong Sender
		PongSender pongSender = new PongSender(this);
		final Thread t = clientChannel.getThreadFactory().newThread(pongSender);
		t.setName("Pong sender");
		t.start();
	}

	/**
	 * Sends an event to the upper layer
	 * 
	 * @param event
	 * @throws AppiaEventException
	 */
	private void SendUpward(Event event) throws AppiaEventException {
		event.setDir(Direction.UP);
		event.setSourceSession(this);
		event.setChannel(channel);
		event.init();
		event.go();
	}

	/**
	 * Sends a stub event to the server
	 * 
	 * @param stubEvent
	 * @throws AppiaEventException
	 */
	private void sendStubEvent(StubEvent stubEvent) throws AppiaEventException {
		stubEvent.storeMessage();
		stubEvent.setDir(Direction.DOWN);
		stubEvent.setChannel(channel);
		stubEvent.setSourceSession(this);
		stubEvent.dest = serverAddress;
		stubEvent.init();
		stubEvent.go();
	}

	/**
	 * It sends an asynchrous pong event to be cathed by VsStubSession
	 *
	 */
	public void sendPong() {
		PongEvent pong = new PongEvent(localAddress);

		try {
			pong.asyncGo(channel, Direction.DOWN);
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Catches the asynchronous pong event and makes it synchronous
	 * 
	 * @param pong
	 * @throws AppiaEventException
	 */
	private void handlePongEvent(PongEvent pong) throws AppiaEventException {
		sendStubEvent(pong);
	}
}