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
 * Created on 27-Jan-2005
 *
 */
package org.continuent.appia.protocols.group.vsyncmultiplexer;

import java.util.HashMap;
import java.util.Iterator;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelClose;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.core.events.channel.EchoEvent;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;


/**
 * @author nunomrc
 *
 */
public class VSyncMultiplexerSession extends Session {

	private HashMap channels;
	private int blockOkCounter;
	
	/**
	 * @param layer
	 */
	public VSyncMultiplexerSession(Layer layer) {
		super(layer);
		channels = new HashMap();
		blockOkCounter = 0;
	}

	public void handle(Event e){
		if(e instanceof EchoEvent)
			handleEchoEvent((EchoEvent)e);
		else if (e instanceof View)
			handleView((View)e);
		else if(e instanceof BlockOk)
			handleBlockOk((BlockOk)e);
		else if (e instanceof ChannelInit)
			handleChannelInit((ChannelInit)e);
		else if (e instanceof ChannelClose)
			handleChannelClose((ChannelClose)e);
		else
			try {
				e.go();
			} catch (AppiaEventException e1) {
				e1.printStackTrace();
			}
	}

	/**
	 * @param close
	 */
	private void handleChannelClose(ChannelClose close) {
		channels.put(close.getChannel(),null);
		try {
			close.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param init
	 */
	private void handleChannelInit(ChannelInit init) {
		channels.put(init.getChannel(),null);
		try {
			init.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param ok
	 */
	private void handleBlockOk(BlockOk ok) {
	  if((--blockOkCounter) == 0)
	    try {
	      ok.go();
	    } catch (AppiaEventException e) {
	      e.printStackTrace();
	    }
	}

	/**
	 * @param view
	 */
	private void handleView(View view) {
	  view.vs.version="MULTI";
	  
	  Iterator it = channels.keySet().iterator();
	  for ( ; it.hasNext() ; ){
	    Channel c = (Channel) it.next();
	    if( ! c.equals(view.getChannel())){
	      try {
	        View copy = new View(view.vs, view.ls, c, view.getDir(), this);
	        copy.go();
	      } catch (AppiaEventException e2) {
	        e2.printStackTrace();
	      }
	    }
	  }
	  
	  try {
	    view.go();
	  } catch (AppiaEventException e1) {
	    e1.printStackTrace();
	  }
	}

	/**
	 * @param event
	 */
	private void handleEchoEvent(EchoEvent echo) {
	  if(echo.getEvent() instanceof BlockOk){
	    blockOkCounter=0;
	    
	    BlockOk blockok=(BlockOk)echo.getEvent();
	    
	    Iterator it = channels.keySet().iterator();
	    for ( ; it.hasNext() ; ){
	      Channel c = (Channel) it.next();
	      if( ! c.equals(echo.getChannel())){
	        try {
	          EchoEvent copy = new EchoEvent(new BlockOk(blockok.group,blockok.view_id), c, echo.getDir(), this);
	          copy.go();
	          blockOkCounter++;
	        } catch (AppiaEventException e2) {
	          e2.printStackTrace();
	        }
	      }
	    }
	    try {
	      echo.go();
	      blockOkCounter++;
	    } catch (AppiaEventException e1) {
	      e1.printStackTrace();
	    }
	    
	  } else {
	    try {
	      echo.go();
	    } catch (AppiaEventException e) {
	      e.printStackTrace();
	    }
	  }
	}
}
