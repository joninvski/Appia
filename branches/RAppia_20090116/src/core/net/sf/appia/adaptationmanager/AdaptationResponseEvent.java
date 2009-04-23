package net.sf.appia.adaptationmanager;

import java.util.ArrayList;
import java.util.List;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.message.Message;

/**
 * 
 * This class defines a AdaptationResponseEvent. This event stores a list of ActionResponses.
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class AdaptationResponseEvent extends SendableEvent{

    private int seqNum;
    private List<ActionResponse> actions;
    
   
    /**
     * Creates a new AdaptationEvent.
     */    
    public AdaptationResponseEvent(){
        super();
        actions = new ArrayList<ActionResponse>();
    }
    
    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @param msg
     * @throws AppiaEventException
     */
    public AdaptationResponseEvent(Channel channel, int dir, Session source, Message msg) throws AppiaEventException {
        super(channel, dir, source, msg);
        actions = new ArrayList<ActionResponse>();
    }

    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @throws AppiaEventException
     */
    public AdaptationResponseEvent(Channel channel, int dir, Session source) throws AppiaEventException {
        super(channel, dir, source);
        actions = new ArrayList<ActionResponse>();
    }

    /**
     * Creates a new AdaptationEvent.
     * @param msg
     */
    public AdaptationResponseEvent(Message msg) {
        super(msg);
        actions = new ArrayList<ActionResponse>();
    }

    /**
     * @param seqNum The seqNum to set.
     */
    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    /**
     * @return Returns the seqNum.
     */
    public int getSeqNum() {
        return seqNum;
    }

    /**
     * @param actions The actions to set.
     */
    public void setActionsResponse(List<ActionResponse> actions) {
        this.actions = actions;
    }

    /**
     * @return Returns the actions.
     */
    public List<ActionResponse> getActionsResponse() {
        return actions;
    }
    
    /**
     * @param a The ActionResponse to add
     */
    public void addAction(ActionResponse a){
        actions.add(a);
    }
    
    /**
     * @param a The ActionResponse to remove
     */
    public void removeAction(ActionResponse a){
        actions.remove(a);
    }
    
}
