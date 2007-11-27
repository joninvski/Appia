
/*
 * JGCS - Group Communication Service.
 * Copyright (C) 2006 Nuno Carvalho, Universidade de Lisboa
 *
 * jgcs@lasige.di.fc.ul.pt
 *
 * Departamento de Informatica, Universidade de Lisboa
 * Bloco C6, Faculdade de Ciências, Campo Grande, 1749-016 Lisboa, Portugal.
 *
 * See COPYING for licensing details.
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

public class ClientOpenGroupTest implements MessageListener, ExceptionListener {

	private DataSession data;

	private Service rpcService;
	
	public ClientOpenGroupTest(DataSession data, Service serviceVSC) {
		this.data = data;
		this.rpcService = serviceVSC;		
	}

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
		for (int i = 0; i < 10; i++) {
			Thread.sleep(1000);
			Message m = data.createMessage();
			byte[] bytes =("C hello world from the client! " +i).getBytes();
			bytes[0] = Constants.CLIENT_MESSAGE;
			m.setPayload(bytes);
			data.send(m, rpcService, null,null,(Annotation[])null);
		}

		Thread.sleep(5000);

	}

	public static void main(String[] args) {
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
