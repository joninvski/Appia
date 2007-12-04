/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2007 University of Lisbon
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
 * Developer(s): Nuno Carvalho.
 */
package net.sf.appia.demo.jgcs.opengroup;

import java.io.IOException;
import java.net.SocketAddress;

import net.sf.appia.jgcs.AppiaGroup;
import net.sf.appia.jgcs.AppiaProtocolFactory;
import net.sf.appia.jgcs.AppiaService;
import net.sf.jgcs.Annotation;
import net.sf.jgcs.ClosedSessionException;
import net.sf.jgcs.ControlListener;
import net.sf.jgcs.ControlSession;
import net.sf.jgcs.DataSession;
import net.sf.jgcs.ExceptionListener;
import net.sf.jgcs.JGCSException;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.NotJoinedException;
import net.sf.jgcs.Protocol;
import net.sf.jgcs.ProtocolFactory;
import net.sf.jgcs.Service;
import net.sf.jgcs.UnsupportedServiceException;
import net.sf.jgcs.membership.BlockListener;
import net.sf.jgcs.membership.BlockSession;
import net.sf.jgcs.membership.MembershipListener;
import net.sf.jgcs.membership.MembershipSession;

/**
 * This class defines a ServerOpenGroupTest.
 * This example shows how to use and configure Appia with jGCS
 * using an open group, where there is a group of servers that accept
 * Messages from external members. This is the server part.
 * 
 * The example only shows how to configure and use, and it only sends
 * dummy messages. It does not intend to implement any algorithm.
 * 
 * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
 * @version 1.0
 */
public class ServerOpenGroupTest implements ControlListener, ExceptionListener,
		MembershipListener, BlockListener {
	
    /*
     * Class that implements a message listener
     */
	private class GroupMessageListener implements MessageListener{

	    /*
	     * All messages arrive here. Messages can be sent from
	     * clients or servers. Messages from servers are totally ordered
	     * and messages from clients arrive async. from another 
	     * communication channel.
	     */
		public Object onMessage(Message msg) {
			byte[] bytes = msg.getPayload();
			if(bytes[0] == Constants.CLIENT_MESSAGE)
				return handleClientMessage(new String(bytes), msg.getSenderAddress());
			else if(bytes[0] == Constants.SERVER_MESSAGE)
				return handleServerMessage(new String(bytes));
			else
				return null;
		}
		
		private Object handleServerMessage(String msg){
			System.out.println("Received message from Server: "+msg);
			return null;
		}
		
		private Object handleClientMessage(String msg, SocketAddress sender){
			System.out.println("Received message from Client "+sender+" :"+msg);
			Message replyMsg = null, groupMsg = null;
			try {
				replyMsg = groupSession.createMessage();
				groupMsg = groupSession.createMessage();
			} catch (ClosedSessionException e) {
				e.printStackTrace();
			}
			// message for the client
			replyMsg.setPayload("Reply!".getBytes());
			byte[] payload = msg.getBytes();
			// message for the servers
			payload[0]=Constants.SERVER_MESSAGE;
			groupMsg.setPayload(payload);
			try {
			    // reply message to sender, using the "clients" Service
				groupSession.send(replyMsg,clients,null,sender,(Annotation[])null);
				// forward message to the servers, using the "group" Service
				groupSession.multicast(groupMsg,group, null, (Annotation[])null);
			} catch (UnsupportedServiceException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	} // end of class GroupMessageListener

	private ControlSession control;
	private DataSession groupSession;
	private Service clients, group;
	
	public ServerOpenGroupTest(ControlSession control, DataSession grSession, Service cl, Service gr) 
	throws JGCSException {
		this.control = control;
		this.groupSession = grSession;
		this.clients = cl;
		this.group = gr;

		// set listeners
		groupSession.setMessageListener(new GroupMessageListener());
		control.setControlListener(this);
		control.setExceptionListener(this);
		if (control instanceof MembershipSession)
			((MembershipSession) control).setMembershipListener(this);
		if (control instanceof BlockSession)
			((BlockSession) control).setBlockListener(this);

	}

	public void onJoin(SocketAddress peer) {
		System.out.println("-- JOIN: " + peer);
	}

	public void onLeave(SocketAddress peer) {
		System.out.println("-- LEAVE: " + peer);
	}

	public void onFailed(SocketAddress peer) {
		System.out.println("-- FAILED: " + peer);
	}

	public void onMembershipChange() {
		try {
			System.out.println("-- NEW MEMBERSHIP: " + ((MembershipSession) control).getMembership());
		} catch (NotJoinedException e) {
			e.printStackTrace();
			groupSession.close();
		}			
	}

	// this notification is issued before a new view
	// a new view will not appear while the flush is not notified
	// (using the blockOk() method). After this, no message can be sent
	// while waiting for a new view.
	public void onBlock() {
		try {
			((BlockSession) control).blockOk();
		} catch (JGCSException e) {
			e.printStackTrace();
		}
	}
	
	public void onExcluded() {
		System.out.println("-- EXCLUDED");
	}

	public void onException(JGCSException arg0) {
		System.out.println("-- EXCEPTION: " + arg0.getMessage());
		arg0.printStackTrace();
	}

	public void run() throws Exception {
	    // joins the group
		control.join();

		// sends some dummy messages
		for (int i = 0; i < 3; i++) {
			Thread.sleep(1000);
			Message m = groupSession.createMessage();
			byte[] bytes = ("_ hello world! " +i).getBytes();
			bytes[0] = Constants.SERVER_MESSAGE;
			m.setPayload(bytes);			
			groupSession.multicast(m, group, null);
		}

		// wait forever.
		Thread.sleep(Long.MAX_VALUE);

		// leaves the group.....
		control.leave();
	}

	public static void main(String[] args) {
	    if(args.length != 1){
	        System.out.println("Must put the xml file name as an argument.");
	        System.exit(1);
	    }
	    
		try {
            ProtocolFactory pf = new AppiaProtocolFactory();
            AppiaGroup g = new AppiaGroup();
            g.setGroupName("group");
            g.setConfigFileName(args[0]);
            Protocol p = pf.createProtocol();
            DataSession session = p.openDataSession(g);
            ControlSession control = p.openControlSession(g);
            Service sc = new AppiaService("rrpc");
            Service sg = new AppiaService("rrpc_group");
			ServerOpenGroupTest test = new ServerOpenGroupTest(control, session, sc, sg);
			test.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
