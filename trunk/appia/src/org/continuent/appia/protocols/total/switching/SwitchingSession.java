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
package org.continuent.appia.protocols.total.switching;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.AppiaException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.EventQualifier;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.core.events.channel.EchoEvent;
import org.continuent.appia.protocols.group.LocalState;
import org.continuent.appia.protocols.group.ViewState;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.SessionProperties;

/**
 * Protocol that implements an algorithm to switch between Total Order algorithms.
 * <p></p>
 * If the parameter <i>firstChannel</i> is defined, then the corresponding channel
 * is used initially. Otherwise, the initial channel is the one associated with
 * the first ChannelInit received by this session.
 * <p></p>
 * Configuration parameters:<br>
 * 
 * topChannel [<string>]: the top channel name. Default: <i>topChannel</i>
 * firstChannel [<string>]: the initial bottom channel name. Default: First channel 
 *  
 * @author Jose Mocito
 * @version 0.7
 */
public class SwitchingSession extends Session implements InitializableSession {

	private static final int NULL_TIMEOUT = 30; // default
	
	/**
     * The channel that contains the top sessions.
	 */
    private Channel topChannel;
    
    /**
     * The bottom channel currently in use.
     */
    private Channel currentChannel;
    
    /**
     * The bottom channel that will replace the current one.
     */
    private Channel nextChannel;
    
    /**
     * List of all bottom channels.
     */
    private Hashtable channelList = new Hashtable();
    
    /**
     * Top channel name. Default is "topChannel".
     */
	private String topChannelName = "topChannel"; // default
    
    /**
     * The name of the channel to be used when the system starts. 
     */
    private String firstChannelName;
	
	private boolean blocked;
	private LocalState ls;
	private ViewState vs;
	
	private boolean switching;
	private boolean[] check;
	private int checked;
	private long localSN = 1;
	private boolean isFirstMsg = true;
	
    private LinkedList nextList = new LinkedList();
    
	public SwitchingSession(Layer layer) {
		super(layer);
	}
	
	public void init(SessionProperties params) {
        if (params.containsKey("topChannel"))
            topChannelName = params.getString("topChannel");
        if (params.containsKey("firstChannel"))
            firstChannelName = params.getString("firstChannel");
	}
	
	public void handle(Event event) {
		if (event instanceof ChannelInit)
			handleChannelInit((ChannelInit) event);
        else if (event instanceof EchoEvent)
            handleEchoEvent((EchoEvent) event);
		else if (event instanceof BlockOk)
			handleBlockOk((BlockOk) event);
		else if (event instanceof View)
			handleView((View) event);
		else if (event instanceof SwitchEvent)
			handleSwitchEvent((SwitchEvent) event);
		else if (event instanceof NullEvent)
			handleNullEvent((NullEvent) event);
		else if (event instanceof NullEventTimer)
			handleNullEventTimer((NullEventTimer) event);
		else if (event instanceof GroupSendableEvent)
			handleGroupSendableEvent((GroupSendableEvent) event);
		else {
			if (event.getDir() == Direction.DOWN) {
				if (currentChannel != null)
					event.setChannel(currentChannel);
			}
			else {
				if (topChannel != null)
					event.setChannel(topChannel);
			}
			try {
				event.setSource(this);
				event.init();
				event.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * Holds the ChannelInit event for the top channel until all the ChannelInit events
     * from the bottom channels reach this session.
     */
	private ChannelInit topChannelInit;

	private void handleChannelInit(ChannelInit init) {
        Channel channel = init.getChannel();
		
		if (init.getChannel().getChannelID().equals(topChannelName)) {
			topChannel = channel;
            
            if (currentChannel == null) {
				topChannelInit = init;
				return;
			}
		}
		else if (firstChannelName != null && init.getChannel().getChannelID().equals(firstChannelName) ||
                 firstChannelName == null && currentChannel == null) {
            currentChannel = channel;
            channelList.put(channel.getChannelID(), channel);
			if (topChannelInit != null)
				try {
					topChannelInit.go();
					topChannelInit = null;
				} catch (AppiaEventException e) {
					e.printStackTrace();
				}
		}
		else
            channelList.put(channel.getChannelID(), channel);
        
		try {
			init.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}
    
    private int bOkCounter = 0;
    
    private void handleEchoEvent(EchoEvent echo) {
        if (echo.getEvent() instanceof BlockOk) {
            bOkCounter++;
            if (bOkCounter == channelList.size()) {
                try {
                    echo.setChannel(topChannel);
                    echo.setSource(this);
                    echo.init();
                    echo.go();
                } catch (AppiaEventException e) {
                    e.printStackTrace();
                }
                bOkCounter = 0;
            }
        }
        else {
            try {
                echo.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
        }
    }
    // TODO: handle BlockOk when switching is ongoing.
	private void handleBlockOk(BlockOk ok) {
        blocked = true;
        Iterator it = channelList.values().iterator();
        while (it.hasNext()) {
            try {
                BlockOk newBOk = new BlockOk((Channel) it.next(), Direction.DOWN, this, vs.group, vs.id);
                newBOk.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
        }
	}
		
	private void handleView(View view) {
        if (vs == null || !view.vs.id.equals(vs.id)) {
            ls = view.ls;
            vs = view.vs;

            view.setChannel(topChannel);
            view.setSource(this);
            try {
                view.init();
                view.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }

            localSN = 1;
            lastDelivered = new long[vs.view.length];

            blocked = false;
        }
	}
	
	private void handleSwitchEvent(SwitchEvent event) {
        if (event.getDir() == Direction.DOWN && event.nextChannelName != null) {
            event.getMessage().pushString(event.nextChannelName);
            try {
                event.setChannel(currentChannel);
                event.setSource(this);
                event.init();
                event.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
		}
        else if (event.getDir() == Direction.UP) {
            startSwitching(event.getMessage().popString());
        }
	}
	        
	private void handleNullEvent(NullEvent event) {
		event.getMessage().pushString(event.getChannel().getChannelID()+" (NULL)");
		event.setChannel(topChannel);
		event.setSource(this);
		try {
			event.init();
			event.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
		
		if (switching) {
			check[event.orig] = true;
			checked++;
			
			if (checked == vs.view.length)
				endSwitching();
		}
	}
	
	private boolean nullFirst;
	private boolean msgFirst;
	
	private void handleNullEventTimer(NullEventTimer timer) {
		if (switching && !msgFirst) {
			try {
				NullEvent nullEvent = new NullEvent(currentChannel,Direction.DOWN,this,vs.group,vs.id);
				nullEvent.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
			nullFirst = true;
		}
	}
	
	private void handleGroupSendableEvent(GroupSendableEvent event) {
		if (event.getDir() == Direction.DOWN) {
			event.getMessage().pushLong(localSN);
			
			if (switching && isFirstMsg && !nullFirst) {
				event.getMessage().pushBoolean(true);
				isFirstMsg = false;
				msgFirst = true;
			}
			else
				event.getMessage().pushBoolean(false);
			
			if (switching) {
				try {
					GroupSendableEvent clone = (GroupSendableEvent) event.cloneEvent();
					clone.setChannel(nextChannel);
					clone.setSource(this);
					clone.init();
					clone.go();					
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				} catch (AppiaEventException e) {
					e.printStackTrace();
				}
			}
			
			try {
				event.setChannel(currentChannel);
				event.setSource(this);
				event.init();
				event.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
			
			localSN++;
		}
		else { // DIRECTION UP
			boolean flag = event.getMessage().popBoolean();
			if (switching) {
				if (event.getChannel() == currentChannel) {
					processCurrent(event, flag);
				}
				else if (event.getChannel() == nextChannel)
					processOther(event);
				
                if (checked >= vs.view.length)
					endSwitching();
			}
			else if (event.getChannel() == currentChannel) {
                tryDeliver(event);
			}
			else if (event.getChannel() == nextChannel) {
				return; // DISCARD
			}
		}
	}
	
	private void processCurrent(GroupSendableEvent event, boolean flag) {
		EventContainer cont = new EventContainer(event.orig,event.getMessage().peekLong(),null);
		if (flag) {
			check[event.orig] = true;
			checked++;
		}
		
        tryDeliver(event);
		
		if (nextList.contains(cont)) {
			nextList.remove(cont);
		}
	}
	
	private void processOther(GroupSendableEvent event) {
		EventContainer cont = new EventContainer(event.orig,event.getMessage().popLong(),event);
		
		if (cont.sn > lastDelivered[event.orig])
			nextList.add(cont);
	}
	
	private long[] lastDelivered;
	
	private void tryDeliver(GroupSendableEvent event) {//, boolean flag) {
		long sn = event.getMessage().popLong();
		if (lastDelivered[event.orig] < sn) {
			lastDelivered[event.orig] = sn;
		
			try {
				event.setChannel(topChannel);
				event.setSource(this);
				event.init();
				event.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * Initiates the switching procedure.
     * 
     * @param nextChannelName The name of the channel to be used in the switching procedure.
     */
    private void startSwitching(String nextChannelName) {
        nextChannel = (Channel) channelList.get(nextChannelName);
        if (nextChannel != null) {
            // Start switching!
            switching = true;
            checked = 0;
            check = new boolean[vs.view.length];
            isFirstMsg = true;
            nullFirst = false;
            msgFirst = false;

            try {
                NullEventTimer nullt = new NullEventTimer(NULL_TIMEOUT,"NullEventTimer",currentChannel,Direction.DOWN,this,EventQualifier.ON);
                nullt.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            } catch (AppiaException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Terminates the switching procedure.
     */
	private void endSwitching() {
		System.out.println("["+ls.my_rank+"] SWITCHING DONE");
		cleanBuffers();
        currentChannel = nextChannel;
        nextChannel = null;
		
		switching = false;
	}

    /**
     * Cleans the remaining messages in the buffers.
     *
     */
	private void cleanBuffers() {
		Iterator it = nextList.iterator();
		while (it.hasNext()) {
			EventContainer cont = (EventContainer) it.next();
			try {
				cont.event.getMessage().pushString(cont.event.getChannel().getChannelID()+ " (buffered)");
				cont.event.setChannel(topChannel);
				cont.event.setSource(this);
				cont.event.init();
				cont.event.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
			lastDelivered[cont.source] = cont.sn;
		}
		nextList.clear();
    }
}
