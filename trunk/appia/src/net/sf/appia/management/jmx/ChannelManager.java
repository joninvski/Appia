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
 * Copyright:    Copyright (c) Nuno Carvalho and Luis Rodrigues<p>
 * Company:      F.C.U.L.<p>
 * @author Nuno Carvalho and Luis Rodrigues
 * @version 1.0
 */

package net.sf.appia.management.jmx;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.management.AppiaManagementException;
import net.sf.appia.management.ManagedSession;
import net.sf.appia.management.SensorSessionListener;


/**
 * This class defines a ChannelManager.
 * 
 * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
 * @version 1.0
 */
public class ChannelManager extends NotificationBroadcasterSupport 
implements DynamicMBean, SensorSessionListener {
	
	private Channel channel;
    private Map<String,Session> managedSessions;
    private Map<String,String[]> parameters;
	
    /**
     * Creates a new ChannelManager.
     * @param ch the managed channel.
     */
	public ChannelManager(Channel ch, Map<String,Session> sessions){
		channel = ch;
        managedSessions = sessions;
        parameters = new Hashtable<String,String[]>();
        for(Entry<String,Session> e : managedSessions.entrySet()){
            if(e.getValue() instanceof ManagedSession)
                parameters.put(e.getKey(), ((ManagedSession)e.getValue()).getAllParameters());
        }
	}

    /**
     * Creates a new ChannelManager.
     * @param ch the managed channel.
     */
    public ChannelManager(Channel ch){
        channel = ch;
        managedSessions = new Hashtable<String,Session>();
    }

    /**
     * Adds a session to manage.
     * @param s the session to manage.
     */
    public void addManagedSession(Session s){
        managedSessions.put(getSessionID(s,channel),s);
    }

    /**
     * Removes a session to manage.
     * @param s the session to manage
     * @return the removed session, or null if no session was removed.
     */
    public Session removeManagedSession(Session s){
        return (Session) managedSessions.remove(getSessionID(s,channel));
    }
    
    private String getSessionID(Session s,Channel ch){
        return s.getClass().getName()+":"+ch.getChannelID();
    }
    
    /**
     * Sets a parameter in one or more sessions of this channel.
     * 
     * @param parameter the parameter name
     * @param value the parameter value
     * @param sessionID the managed session
     * @see net.sf.appia.management.jmx.ChannelManagerMBean#setParameter(String, String, String)
     */
	public void setParameter(String parameter, String value, String sessionID) 
    throws AppiaManagementException {
        final ManagedSession session = (ManagedSession) managedSessions.get(sessionID);
        if(session == null)
            throw new AppiaManagementException("Session with ID '"+sessionID+"' does not exist");
        session.setParameter(parameter,value);
	}

    /**
     * Get the value of a parameter.
     * 
     * @param parameter the parameter to query
     * @param sessionID the managed session
     * @return the value of the parameter.
     * @see net.sf.appia.management.jmx.ChannelManagerMBean#getParameter(String, String)
     */
    public String getParameter(String parameter, String sessionID) throws AppiaManagementException {
        final ManagedSession session = (ManagedSession) managedSessions.get(sessionID);
        if(session == null)
            throw new AppiaManagementException("Session with ID '"+sessionID+"' does not exist");
        return session.getParameter(parameter);
    }

    /**
     * Callback that receives a notification from the channel. Received the notification and pushes it
     * to the registered clients.
     * 
     * @param notification the received notification
     * @see net.sf.appia.management.SensorSessionListener#onNotification(javax.management.Notification)
     */
	public void onNotification(Notification notification) {
		notification.setSource(this);
		sendNotification(notification);
	}

    /**
     * Gets the name of the managed channel.
     * 
     * @see net.sf.appia.management.jmx.ChannelManagerMBean#getChannelName()
     */
	public String getChannelName() {
		return channel.getChannelID();
	}

    public boolean getStarted() {
        return channel.isStarted();
    }
    
    public int getUsedMemory(){
        if(channel.getMemoryManager() == null)
            return -1;
        else
            return channel.getMemoryManager().used();
    }

    public Object getAttribute(String arg0) throws AttributeNotFoundException, MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        return null;
    }

    public AttributeList getAttributes(String[] arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public MBeanInfo getMBeanInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object invoke(String arg0, Object[] arg1, String[] arg2) throws MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setAttribute(Attribute arg0) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        
    }

    public AttributeList setAttributes(AttributeList arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
