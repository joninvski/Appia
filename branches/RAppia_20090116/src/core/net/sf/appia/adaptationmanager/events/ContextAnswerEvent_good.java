package net.sf.appia.adaptationmanager.events;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.message.Message;
import net.sf.appia.core.reconfigurator.utils.Attribute;


public class ContextAnswerEvent_good extends SendableEvent{

    private Attribute[] addaptableParams;
    
   
    /**
     * Creates a new stackCompositionEvent.
     */    
    public ContextAnswerEvent_good(){
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
    public ContextAnswerEvent_good(Channel channel, int dir, Session source, Message msg) throws AppiaEventException {
        super(channel, dir, source, msg);
    }

    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @throws AppiaEventException
     */
    public ContextAnswerEvent_good(Channel channel, int dir, Session source) throws AppiaEventException {
        super(channel, dir, source);
    }

    /**
     * Creates a new AdaptationEvent.
     * @param msg
     */
    public ContextAnswerEvent_good(Message msg) {
        super(msg);
    }

    /**
     * @param addaptableParams The addaptableParams to set.
     */
    public void setAddaptableParams(Attribute[] addaptableParams) {
        this.addaptableParams = addaptableParams;
    }

    /**
     * @return Returns the addaptableParams.
     */
    public Attribute[] getAddaptableParams() {
        return addaptableParams;
    }

     
    
}
