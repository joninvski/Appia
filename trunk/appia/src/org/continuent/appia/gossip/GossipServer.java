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
 /**
 * Title:        Appia<p>
 * Description:  Protocol development and composition framework<p>
 * Copyright:    Copyright (c) Alexandre Pinto & Hugo Miranda & Luis Rodrigues<p>
 * Company:      F.C.U.L.<p>
 * @author Alexandre Pinto & Hugo Miranda & Luis Rodrigues
 * @version 1.0
 */

package org.continuent.appia.gossip;
import org.continuent.appia.core.*;
import org.continuent.appia.protocols.fifo.FifoLayer;
import org.continuent.appia.protocols.gossipServer.GossipServerLayer;
import org.continuent.appia.protocols.gossipServer.GossipServerSession;
import org.continuent.appia.protocols.group.bottom.GroupBottomLayer;
import org.continuent.appia.protocols.group.heal.GossipOutLayer;
import org.continuent.appia.protocols.group.heal.GossipOutSession;
import org.continuent.appia.protocols.group.heal.HealLayer;
import org.continuent.appia.protocols.group.inter.InterLayer;
import org.continuent.appia.protocols.group.intra.IntraLayer;
import org.continuent.appia.protocols.group.leave.LeaveLayer;
import org.continuent.appia.protocols.group.stable.StableLayer;
import org.continuent.appia.protocols.group.suspect.SuspectLayer;
import org.continuent.appia.protocols.group.sync.VSyncLayer;
import org.continuent.appia.protocols.udpsimple.UdpSimpleLayer;
import org.continuent.appia.xml.utils.SessionProperties;


/**
 * This class implements a gossip server for the group communication 
 * protocols.
 */ 
public class GossipServer {

  public static final String DEFAULT_UDP_LAYER=GossipOutSession.DEFAULT_UDP_LAYER;
  
  private static String udp_layer=DEFAULT_UDP_LAYER;
  private static boolean solo=false;
  
  public static void main(String[] args) {

    SessionProperties params=new SessionProperties();

    if (!parse(args,0,params))
       System.exit(1);

    GossipServerLayer glayer=new GossipServerLayer();
    GossipServerSession gsession=(GossipServerSession) glayer.createSession();

    gsession.init(params);
    
    try {
      Layer[] l={
          (Layer)Class.forName(udp_layer).newInstance(),
          new FifoLayer(),
          glayer
      };

      QoS qos=new QoS("Gossip Client QoS",l);
      Channel channel=qos.createUnboundChannel("Gossip Channel");

      ChannelCursor cc=channel.getCursor();
      cc.top();
      cc.setSession(gsession);
      
      channel.start();
    } catch (AppiaException ex) {
      ex.printStackTrace();
      System.err.println("Impossible to create and/or start client channel");
      System.exit(1);
    } catch (InstantiationException e) {
      e.printStackTrace();
      System.err.println("Impossible to create and/or start client channel");
      System.exit(1);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      System.err.println("Impossible to create and/or start client channel");
      System.exit(1);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.err.println("Impossible to create and/or start client channel");
      System.exit(1);
    }

    if (solo) {
      Appia.run();
      return;
    }
    
    try {
      Layer[] l={
          new UdpSimpleLayer(), 
          new FifoLayer(),
          new GroupBottomLayer(),
          new GossipOutLayer(),
          new SuspectLayer(),
          new IntraLayer(),
          new InterLayer(),
          new HealLayer(),
          new StableLayer(),
          new LeaveLayer(),
          new VSyncLayer(),
          glayer
      };

      QoS qos=new QoS("Gossip Group QoS",l);
      Channel channel=qos.createUnboundChannel("Gossip Group Channel");

      ChannelCursor cc=channel.getCursor();
      cc.top();
      cc.setSession(gsession);
      
      channel.start();
    } catch (AppiaException ex) {
      ex.printStackTrace();
      System.err.println("Impossible to create and/or start group channel");
      System.exit(1);
    }

    Appia.run();
  }

  // Must end with a '-'
  private static final String sessionParams="-port-gossip-remove_time-timer-";
    
  private static boolean parse(String[] args, int i, SessionProperties params) {

    if (i >= args.length)
      return true;

    if (sessionParams.indexOf(args[i]+"-") >= 0) {
      if (i+1 >= args.length) {
        System.err.println("Missing port value");
        printHelp();
        return false;
      }
      
      params.put(args[i].substring(1), args[i+1]);
      return parse(args,i+2,params);
    }

    if (args[i].equals("-debug")) {
      params.put("debug","true");
      return parse(args,i+1,params);
    }
    
    if (args[i].equals("-solo")) {
      solo=true;
      return parse(args, i+1, params);
    }

    if (args[i].equals("-udp")) {
      if (i+1 >= args.length) {
        System.err.println("Missing UDP layer name.");
        printHelp();
        return false;
      }
 
      udp_layer=args[i+1];
      return parse(args,i+2,params);
    }
    
    if (args[i].equals("-help")) {
      printHelp();
      return true;
    }

    System.err.println("Unknown argument: "+args[i]);
    printHelp();
    return false;
  }

  private static void printHelp() {
    System.out.println("java GossipServer [-port <port>] [-udp <udp_layer_name>] [-debug] [-help] [-solo] [-gossips <ip>[:<port>][,...]]");
  }
}
