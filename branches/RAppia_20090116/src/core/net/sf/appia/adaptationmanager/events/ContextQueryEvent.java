package net.sf.appia.adaptationmanager.events;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.message.Message;


public class ContextQueryEvent extends SendableEvent{

    private String id;
  
    /**
     * Creates a new stackCompositionEvent.
     */    
    public ContextQueryEvent(){
        super();
    }
    
    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @param msg
     * @throws AppiaEventException
     */
    public ContextQueryEvent(Channel channel, int dir, Session source, Message msg) throws AppiaEventException {
        super(channel, dir, source, msg);
    }

    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @throws AppiaEventException
     */
    public ContextQueryEvent(Channel channel, int dir, Session source) throws AppiaEventException {
        super(channel, dir, source);
    }

    /**
     * Creates a new AdaptationEvent.
     * @param msg
     */
    public ContextQueryEvent(Message msg) {
        super(msg);
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

  
    
}
