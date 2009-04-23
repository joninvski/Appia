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
 * This class defines a AdaptationEvent which stores a list of Actions. When the adaptation manager 
 * wants to order the reconfigurator of the node to perform some changes in the communication stack,
 * an AdaptationEvent is sent with the list of the Actions to be executed.
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class AdaptationEvent extends SendableEvent{

    private int seqNum;
    private List<Action> actions;
    
   
    /**
     * Creates a new AdaptationEvent.
     */    
    public AdaptationEvent(){
        super();
        actions = new ArrayList<Action>();
    }
    
    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @param msg
     * @throws AppiaEventException
     */
    public AdaptationEvent(Channel channel, int dir, Session source, Message msg) throws AppiaEventException {
        super(channel, dir, source, msg);
        actions = new ArrayList<Action>();
    }

    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @throws AppiaEventException
     */
    public AdaptationEvent(Channel channel, int dir, Session source) throws AppiaEventException {
        super(channel, dir, source);
        actions = new ArrayList<Action>();
    }

    /**
     * Creates a new AdaptationEvent.
     * @param msg
     */
    public AdaptationEvent(Message msg) {
        super(msg);
        actions = new ArrayList<Action>();
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
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    /**
     * @return Returns the actions.
     */
    public List<Action> getActions() {
        return actions;
    }
    
    /**
     * @param a The action to add.
     */
    public void addAction(Action a){
        actions.add(a);
    }
    
    /**
     * @param a The action to remove.
     */
    public void removeAction(Action a){
        actions.remove(a);
    }
    
}
