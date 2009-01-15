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

import java.net.SocketAddress;

import net.sf.appia.jgcs.AppiaGroup;
import net.sf.appia.jgcs.AppiaProtocolFactory;
import net.sf.appia.jgcs.AppiaService;
import net.sf.jgcs.Annotation;
import net.sf.jgcs.DataSession;
import net.sf.jgcs.ExceptionListener;
import net.sf.jgcs.JGCSException;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.Protocol;
import net.sf.jgcs.ProtocolFactory;
import net.sf.jgcs.Service;

/**
 * 
 * This class defines a ClientOpenGroupTest
 * This example shows how to use and configure Appia with jGCS
 * using an open group, where there is a group of servers that accept
 * Messages from external members. This is the (external) client part.
 * 
 * The example only shows how to configure and use, and it only sends
 * dummy messages. It does not intend to implement any algorithm.
 * 
 * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
 * @version 1.0
 */
public class ClientOpenGroupTest implements MessageListener, ExceptionListener {

    // only the data session is used
	private DataSession data;
	private Service rpcService;
	
	public ClientOpenGroupTest(DataSession data, Service serviceVSC) {
		this.data = data;
		this.rpcService = serviceVSC;		
	}

	// messages are received here.
	public Object onMessage(Message msg) {
		System.out.println("Message from "+msg.getSenderAddress()+": "+new String(msg.getPayload()));
		return null;
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
	
	public void onException(JGCSException arg0) {
		System.out.println("-- EXCEPTION: " + arg0.getMessage());
		arg0.printStackTrace();
	}

	public void run() throws Exception {
	    // sends dummy messages with no destination address.
	    // an underlying protocol (in Appia) will discover
	    // one address that belongs to the group and send the message
	    // to that member.
	    // Replies can be received from any group member.
		for (int i = 0; true; i++) {
			Thread.sleep(1000);
			Message m = data.createMessage();
			byte[] bytes =("C hello world from the client! " +i).getBytes();
			bytes[0] = Constants.CLIENT_MESSAGE;
			m.setPayload(bytes);
			data.send(m, rpcService, null,null,(Annotation[])null);
		}

		// waits 5 seconds before ending.
//		Thread.sleep(5000);

	}

	public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Must put the xml file name as an argument.");
            System.exit(1);
        }
		try {
			ProtocolFactory pf = new AppiaProtocolFactory();
			AppiaGroup g = new AppiaGroup();
			g.setConfigFileName(args[0]);
			g.setGroupName("group");
			Protocol p = pf.createProtocol();
			DataSession session = p.openDataSession(g);
			Service service = new AppiaService("rrpc");
			ClientOpenGroupTest test = new ClientOpenGroupTest(session, service);
			session.setMessageListener(test);
			session.setExceptionListener(test);
			test.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
