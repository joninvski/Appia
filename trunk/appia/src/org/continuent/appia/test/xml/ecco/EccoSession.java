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
 /*
 * Created on Mar 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.continuent.appia.test.xml.ecco;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.common.AppiaThreadFactory;
import org.continuent.appia.protocols.common.RegisterSocketEvent;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.SessionProperties;


/**
 * @author jmocito
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class EccoSession extends Session implements InitializableSession {
	
	Channel channel;
	InetSocketAddress local;
	InetSocketAddress remote;
	
	MyShell shell;
	
	public EccoSession(EccoLayer l) {
		super(l);
	}
	
	// TODO Throw exception in case of invalid parameters
	public void init(SessionProperties params) {
		int localPort = Integer.parseInt(params.getProperty("localport"));
		String remoteHost = params.getProperty("remotehost");
		int remotePort = Integer.parseInt(params.getProperty("remoteport"));
		try {
			local = new InetSocketAddress(InetAddress.getLocalHost(),localPort);
			this.remote = 
				new InetSocketAddress(InetAddress.getByName(remoteHost),remotePort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init(int localPort, InetSocketAddress remote){
		try {
			local = new InetSocketAddress(InetAddress.getLocalHost(),localPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		this.remote = remote;
	}
	
	public void handle(Event ev) {
		if (ev instanceof ChannelInit)
			handleChannelInit((ChannelInit) ev);
		else if (ev instanceof MyEccoEvent)
			handleMyEchoEvent((MyEccoEvent) ev);
		else
			try {
				ev.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
	}
	
	public void handleChannelInit(ChannelInit init) {
		channel = init.getChannel();
		try {
			init.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
		
		shell = new MyShell(channel);
		AppiaThreadFactory.getThreadFactory().newThread(shell,"Ecco").start();
		
		/*
		 * Este evento serve para registar um socket na camada usada como
		 * interface entre o Appia e os sockets.
		 */
		try {
			RegisterSocketEvent rse = new RegisterSocketEvent(channel,Direction.DOWN,this,local.getPort());
			rse.go();
		} catch (AppiaEventException e1) {
			e1.printStackTrace();
		}
	}
	
	public void handleMyEchoEvent(MyEccoEvent echo) {
		Message message = echo.getMessage();
		if (echo.getDir() == Direction.DOWN) {
			String text = echo.getText();
			message.pushString(text);
			
			echo.source = local;
			echo.dest = remote;
			try {
				echo.setSource(this);
				echo.init();
				echo.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		}
		else {
			String text = message.popString();
			echo.setText(text);
			System.out.print("\n"+echo.getText()+"\n> ");
		}
	}
}
