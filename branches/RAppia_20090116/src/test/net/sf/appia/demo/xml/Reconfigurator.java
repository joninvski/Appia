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
package net.sf.appia.demo.xml;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import net.sf.appia.core.Appia;
import net.sf.appia.core.AppiaCursorException;
import net.sf.appia.core.AppiaDuplicatedSessionsException;
import net.sf.appia.core.AppiaInvalidQoSException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.ChannelCursor;
import net.sf.appia.core.Layer;
import net.sf.appia.core.QoS;
import net.sf.appia.protocols.reconfigurator.ReconfiguratorLayer;
import net.sf.appia.protocols.reconfigurator.ReconfiguratorSession;
import net.sf.appia.test.xml.ecco.EccoLayer;
import net.sf.appia.test.xml.ecco.EccoSession;
import net.sf.appia.xml.AppiaXML;

import org.xml.sax.SAXException;


/**
 * @author cfonseca
 */
public class Reconfigurator {
	
    private static final int NUMBER_OF_ARGS = 6;
    
    private Reconfigurator() {}
    
    private static Layer a =  new net.sf.appia.protocols.tcpcomplete.TcpCompleteLayer();
    private static Layer a1 =  new net.sf.appia.protocols.tcpcomplete.TcpCompleteLayer();
    private static Layer b =  new ReconfiguratorLayer();
    private static Layer c = new EccoLayer();
    
    
	private static Layer[] qos={a,b,c};
	private static Layer[] qos_rec = {a1,b};
	
	public static void main(String[] args) {
		
		if (args.length == 1) {
			final File xmlfile = new File(args[0]);
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
		else if (args.length == NUMBER_OF_ARGS) {
			final int localport = Integer.parseInt(args[0]);
			final String remotehost = args[1];
			final int remoteport = Integer.parseInt(args[2]);
			
			final int localport2 = Integer.parseInt(args[3]);
			final String remotehost2 = args[4];
			final int remoteport2 = Integer.parseInt(args[5]);
			
			/* Create a QoS */
		    QoS myQoS=null;
		    QoS recQoS=null;
		    try {
		      myQoS=new QoS("Appl QoS",qos);
		      recQoS=new QoS("Rec QoS", qos_rec);
		    } catch(AppiaInvalidQoSException ex) {
		      System.err.println("Invalid QoS");
		      System.err.println(ex.getMessage());
		      System.exit(1);
		    }
		    
		    /* Create a channel. Uses default event scheduler. */
		    final Channel myChannel=myQoS.createUnboundChannel("App Channel");
		   final Channel recChannel=recQoS.createUnboundChannel("Control Channel");
		    /* Application Session requires special arguments: qos and port.
		       A session is created and binded to the stack. Remaining ones
		       are created by default
		     */
		    final EccoSession es=(EccoSession)qos[qos.length-1].createSession();
		    final ReconfiguratorSession ams=(ReconfiguratorSession)qos_rec[qos_rec.length-1].createSession();
		    try {

                es.init(
                        localport,
                        new InetSocketAddress(InetAddress.getByName(remotehost),remoteport));
		        
		        ams.init(
						localport2,
						new InetSocketAddress(InetAddress.getByName(remotehost2),remoteport2));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		    
		    final ChannelCursor cc=myChannel.getCursor();
		    final ChannelCursor cc2 = recChannel.getCursor();
		    
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
		    	
		    	 
	              cc2.top();
	              cc.down();
	              cc2.setSession(cc.getSession());
	             cc.bottom();
	             cc2.bottom();
	             cc2.setSession(cc.getSession());
		    	
		    	//recChannel.start();
		    } catch(AppiaDuplicatedSessionsException ex) {
		    	System.err.println("Sessions binding strangely resulted in "+
		    			"one single sessions occurring more than "+
						"once in a channel");
		    	System.exit(1);
		    } catch (AppiaCursorException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
