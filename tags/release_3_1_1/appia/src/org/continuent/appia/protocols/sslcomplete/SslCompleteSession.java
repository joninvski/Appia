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
 package org.continuent.appia.protocols.sslcomplete;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Random;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.events.AppiaMulticast;
import org.continuent.appia.core.events.SendableEvent;
import org.continuent.appia.protocols.common.InetWithPort;
import org.continuent.appia.protocols.tcpcomplete.AcceptReader;
import org.continuent.appia.protocols.tcpcomplete.TcpCompleteSession;
import org.continuent.appia.protocols.utils.HostUtils;


/**
 * @author pedrofrv
 *
 */
public class SslCompleteSession extends TcpCompleteSession {
  
  private SSLServerSocketFactory ssf=null;
  private SSLSocketFactory sf=null;
  
  /**
   * Constructor for NewTcpSession.
   * @param layer
   */
  public SslCompleteSession(Layer layer) {
    super(layer);
  }
  
  public void handle(Event e){
    if(e instanceof SendableEvent)
      handleSendable((SendableEvent)e);
    else if(e instanceof SslRegisterSocketEvent)
      handleSslRegisterSocket((SslRegisterSocketEvent)e);
    else
      super.handle(e);
  }
  
  private void handleSendable(SendableEvent e){
    Object[] valids=null;
    
    if(SslCompleteConfig.debugOn)
      debug("preparing to send ::"+e);
    
    if (e.dest instanceof AppiaMulticast) {
      Object[] dests=((AppiaMulticast)e.dest).getDestinations();
      for (int i=0 ; i < dests.length ; i++) {
        if (dests[i] instanceof InetWithPort) {
          if (!validate((InetWithPort)dests[i], e.getChannel())) {
            if (valids == null) {
              valids=new Object[dests.length];
              System.arraycopy(dests, 0, valids, 0, i);
            }
            valids[i]=null;
            sendUndelivered(e.getChannel(), dests[i]);
          } else {
            if (valids != null)
              valids[i]=dests[i];
          }
        } else
          sendUndelivered(e.getChannel(),dests[i]);
      }
    } else if (e.dest instanceof InetWithPort) {
      if (!validate((InetWithPort)e.dest, e.getChannel()))
        sendUndelivered(e.getChannel(), e.dest);
    } else {
      sendUndelivered(e.getChannel(),e.dest);
    }
    
    if (valids != null) {
      int i,tam=0,j=0;
      for (i=0 ; i < valids.length ; i++)
        if (valids[i] != null)
          tam++;
      Object[] trimmed_dests=new Object[tam];
      for (i=0 ; i < valids.length ; i++) {
        if (valids[i] != null) {
          trimmed_dests[j]=valids[i];
          j++;
        }
      }
      AppiaMulticast dest=new AppiaMulticast(((AppiaMulticast)e.dest).getMulticastAddress(), trimmed_dests);
      e.dest=dest;
    }
    
    super.handle(e);
  }
  
  private boolean validate(InetWithPort dest, Channel channel) {
    
    try {
      //check if the socket exist int the opensockets created by us
      if(existsSocket(ourReaders,dest)){
        if(SslCompleteConfig.debugOn)
          debug("our sslsocket, sending...");
        return true;
      }
      else{//if not
        //check if socket exist in sockets created by the other
        if(existsSocket(otherReaders,dest)){
          if(SslCompleteConfig.debugOn)
            debug("other sslsocket, sending...");
          return true;
        }
        else{//if not
          //create new socket and put it opensockets created by us
          if(createSSLSocket(ourReaders,dest,channel) != null)
            if(SslCompleteConfig.debugOn)
              debug("created new sslsocket, sending...");
          return true;
        }
      }
    } catch (IOException ex) {
      if(SslCompleteConfig.debugOn) {
        ex.printStackTrace();
        debug("o no "+dest.toString()+" falhou");
      }
      sendUndelivered(channel,dest);
      removeSocket(dest);
    }
    
    return false;
  }
  
  private void handleSslRegisterSocket(SslRegisterSocketEvent e){
    if(SslCompleteConfig.debugOn)
      debug("received SRSE");
    
    SSLServerSocket ss= null;
    
    try{
      SSLContext ctx = SSLContext.getInstance(e.protocol);
      
      if (e.keystoreFile != null) {
        KeyStore ks = KeyStore.getInstance(e.keyStore);
        ks.load(new FileInputStream(e.keystoreFile),e.passphrase);
        
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(e.certificateManagers);
        kmf.init(ks,e.passphrase);
        TrustManagerFactory tmf=TrustManagerFactory.getInstance(e.certificateManagers);
        tmf.init(ks);
        
        ctx.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null);
        ssf = ctx.getServerSocketFactory();
        sf = ctx.getSocketFactory();
        
      } else {
        
        ctx.init(null,null,null);
        ssf = new CustomSSLServerSocketFactory(ctx,true);
        sf = new CustomSSLSocketFactory(ctx,true);
      }
      
      if (SslCompleteConfig.debugOn) {
        int i;
        String[] suites;
        debug("--> Server Supported cipher suites");
        suites=ssf.getSupportedCipherSuites();
        for (i=0 ; i < suites.length ; i++)
          debug(suites[i]);
        debug("--> Server Default cipher suites");
        suites=ssf.getDefaultCipherSuites();
        for (i=0 ; i < suites.length ; i++)
          debug(suites[i]);
        debug("--> Client Supported cipher suites");
        suites=sf.getSupportedCipherSuites();
        for (i=0 ; i < suites.length ; i++)
          debug(suites[i]);
        debug("--> Client Default cipher suites");
        suites=sf.getDefaultCipherSuites();
        for (i=0 ; i < suites.length ; i++)
          debug(suites[i]);
      }
      
    }
    catch(Exception ex){
      e.port=-1;
      ex.printStackTrace();
      return;
    }
    
    if(e.port == SslRegisterSocketEvent.FIRST_AVAILABLE){
      try {
        ss = (SSLServerSocket)ssf.createServerSocket(0);
        e.port = ss.getLocalPort();
      } catch (IOException ex) {
        e.port = -1;
      }
    }
    else if(e.port == SslRegisterSocketEvent.RANDOMLY_AVAILABLE){
      Random rand = new Random();
      int p;
      boolean done = false;
      
      while(!done){
        p = rand.nextInt(Short.MAX_VALUE);
        
        try {
          ss = (SSLServerSocket)ssf.createServerSocket(p);
          done = true;
          e.port = ss.getLocalPort();
        } catch(IllegalArgumentException ex){
        } catch (IOException ex) {
        }
      }
    } else if (e.port > 0) {
      try {
        ss = (SSLServerSocket)ssf.createServerSocket(e.port);
      } catch (IOException ex) {
        e.port = -1;
      }
    }
    
    if (e.port > 0) {
      //create accept thread int the request port.
      acceptThread = new AcceptReader(ss,this,e.getChannel(),socketLock);
      acceptThread.start();
      
      ourPort = ss.getLocalPort();
      if(SslCompleteConfig.debugOn)
        debug("Our port is "+ourPort);
      
      e.localHost=HostUtils.getLocalAddress();
      e.error=false;
    } else
      e.error=true;
    
    //		send RegisterSocketEvent
    e.setDir(Direction.invert(e.getDir()));
    e.setSource(this);
    
    try {
      e.init();
      e.go();
    } catch (AppiaEventException ex) {
      ex.printStackTrace();
    }
  }
  
  //create socket, put in hashmap and create thread
  public Socket createSSLSocket(HashMap hm,InetWithPort iwp,Channel channel) throws IOException{
    synchronized(socketLock){
      Socket newSocket = null;
      
      if (sf == null)
        return null;
      
      //Create SslSocket.
      newSocket  = (SSLSocket)sf.createSocket(iwp.host, iwp.port);
      
      newSocket.setTcpNoDelay(true);
      
      byte bPort[]= intToByteArray(ourPort);
      
      newSocket.getOutputStream().write(bPort);
      if(SslCompleteConfig.debugOn)
        debug("Sending our original port "+ourPort);
      
      addSocket(hm,iwp,newSocket,channel);
      
      return newSocket;
    }
  }

  private void debug(String msg){
    System.out.println("[SslComplete] ::"+msg);
  }
}
