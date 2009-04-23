package net.sf.appia.adaptationmanager.events;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.message.Message;


public class ContextAnswerEvent extends SendableEvent{

   //tem de ser passado para um tipo mais generico -> o good
    private Hashtable<String, Channel> channelList = new Hashtable<String, Channel>();
    private Hashtable<String, Session> sessionList = new Hashtable<String, Session>();

    /**
     * Creates a new stackCompositionEvent.
     */    
    public ContextAnswerEvent(){
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
    public ContextAnswerEvent(Channel channel, int dir, Session source, Message msg) throws AppiaEventException {
        super(channel, dir, source, msg);
    }

    /**
     * Creates a new AdaptationEvent.
     * @param channel
     * @param dir
     * @param source
     * @throws AppiaEventException
     */
    public ContextAnswerEvent(Channel channel, int dir, Session source) throws AppiaEventException {
        super(channel, dir, source);
    }

    /**
     * Creates a new AdaptationEvent.
     * @param msg
     */
    public ContextAnswerEvent(Message msg) {
        super(msg);
    }

    /**
     * @param channelList The channelList to set.
     */
    public void setChannelList(Hashtable<String, Channel> channelList) {
        this.channelList = channelList;
    }

    /**
     * @return Returns the channelList.
     */
    public Hashtable<String, Channel> getChannelList() {
        return channelList;
    }

    /**
     * @param sessionList The sessionList to set.
     */
    public void setSessionList(Hashtable<String, Session> sessionList) {
        this.sessionList = sessionList;
    }

    /**
     * @return Returns the sessionList.
     */
    public Hashtable<String, Session> getSessionList() {
        return sessionList;
    }

  
    
}
