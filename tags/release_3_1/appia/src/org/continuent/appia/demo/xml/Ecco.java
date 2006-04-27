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
package org.continuent.appia.demo.xml;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.continuent.appia.core.Appia;
import org.continuent.appia.core.AppiaCursorException;
import org.continuent.appia.core.AppiaDuplicatedSessionsException;
import org.continuent.appia.core.AppiaInvalidQoSException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.ChannelCursor;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.QoS;
import org.continuent.appia.protocols.common.InetWithPort;
import org.continuent.appia.test.xml.ecco.EccoLayer;
import org.continuent.appia.test.xml.ecco.EccoSession;
import org.continuent.appia.xml.AppiaXML;
import org.xml.sax.SAXException;


/**
 * @author jmocito
 */
public class Ecco {
	
	private static Layer[] qos={
		    new org.continuent.appia.protocols.tcpcomplete.TcpCompleteLayer(),
		    new EccoLayer()
	};
	
	public static void main(String[] args) {
		
		if (args.length == 1) {
			File xmlfile = new File(args[0]);
			try {
				AppiaXML.load(xmlfile);
				Appia.run();
				//AppiaXML.loadAndRun(xmlfile);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (args.length == 3) {
			int localport = Integer.parseInt(args[0]);
			String remotehost = args[1];
			int remoteport = Integer.parseInt(args[2]);
			
			/* Create a QoS */
		    QoS myQoS=null;
		    try {
		      myQoS=new QoS("Appl QoS",qos);
		    } catch(AppiaInvalidQoSException ex) {
		      System.err.println("Invalid QoS");
		      System.err.println(ex.getMessage());
		      System.exit(1);
		    }
		    
		    /* Create a channel. Uses default event scheduler. */
		    Channel myChannel=myQoS.createUnboundChannel("Appl Channel");
		    
		    /* Application Session requires special arguments: qos and port.
		       A session is created and binded to the stack. Remaining ones
		       are created by default
		     */
		    
		    EccoSession es=(EccoSession)qos[qos.length-1].createSession();
		    try {
				es.init(
						localport,
						new InetWithPort(InetAddress.getByName(remotehost),remoteport));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    ChannelCursor cc=myChannel.getCursor();
		    /* Application is the last session of the array. Positioning
		       in it is simple */
		    try {
		      cc.top();
		      cc.setSession(es);
		    } catch(AppiaCursorException ex) {
		      System.err.println("Unexpected exception in main. Type code:"+
		      ex.type);
		      System.exit(1);
		    }
		    
		    /* Remaining ones are created by default. Just tell the channel to start */
		    try {
		    	myChannel.start();
		    } catch(AppiaDuplicatedSessionsException ex) {
		    	System.err.println("Sessions binding strangely resulted in "+
		    			"one single sessions occurring more than "+
						"once in a channel");
		    	System.exit(1);
		    }
		    Appia.run();
		}
		else {
			System.out.println("Invalid number of arguments!");
			System.out.println(
					"Usage:\tjava Ecco <localport> <remotehost> <remoteport>");
			System.out.println("\tjava Ecco <xml_file>");
		}
	}
}
