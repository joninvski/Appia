
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

public class ServerOpenGroupTest implements ControlListener, ExceptionListener,
		MembershipListener, BlockListener {
	
	private 
	
	class GroupMessageListener implements MessageListener{

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
			System.out.println("Received message from Client: "+msg);
			Message replyMsg = null;
			try {
				replyMsg = groupSession.createMessage();
			} catch (ClosedSessionException e) {
				e.printStackTrace();
			}
			replyMsg.setPayload("Reply!".getBytes());
			try {
				groupSession.send(replyMsg,clients,null,sender,(Annotation[])null);
			} catch (UnsupportedServiceException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}

	private ControlSession control;

	private DataSession groupSession;

	private Service clients, group;
	
	public ServerOpenGroupTest(ControlSession control, DataSession grSession, Service cl, Service gr) 
	throws JGCSException {
		this.control = control;
		this.groupSession = grSession;
		this.clients = cl;
		this.group = gr;

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
		control.join();

		for (int i = 0; i < 3; i++) {
			Thread.sleep(1000);
			Message m = groupSession.createMessage();
			byte[] bytes = ("_ hello world! " +i).getBytes();
			bytes[0] = Constants.SERVER_MESSAGE;
			m.setPayload(bytes);
			
			groupSession.multicast(m, group, null);
		}

		Thread.sleep(Long.MAX_VALUE);

		control.leave();
	}

	public static void main(String[] args) {
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

