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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
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
	
    private class Operation{
        MBeanOperationInfo operation;
        ManagedSession session;
        Operation(MBeanOperationInfo op, ManagedSession s){
            operation = op;
            session = s;
        }
    }
    
	private Channel channel;
    // session name -> session
    private Map<String,Session> managedSessions;
    // session name -> operations inside session
    // private Map<String,String[]> parameters;
    // exported operation name -> session to call
    private Map<String,Operation> operations;
    private ArrayList<MBeanOperationInfo>mboi;

    /**
     * Creates a new ChannelManager.
     * @param ch the managed channel.
     */
    public ChannelManager(Channel ch){
        channel = ch;
        managedSessions = new Hashtable<String,Session>();
        operations = new Hashtable<String,Operation>();
        mboi = new ArrayList<MBeanOperationInfo>();
    }

    /**
     * Adds a session to manage.
     * @param s the session to manage.
     */
    public void addManagedSession(Session s){
        managedSessions.put(s.getId(),s);
        if(s instanceof ManagedSession){
            final ManagedSession ms = (ManagedSession) s;
            final MBeanOperationInfo[] ops = ms.getAllParameters(s.getId()+":");
            for(int i=0; i<ops.length; i++){
                operations.put(ops[i].getName(), new Operation(ops[i],ms));
                mboi.add(ops[i]);
            }
        }
    }

    /**
     * Removes a session to manage.
     * @param s the session to manage
     * @return the removed session, or null if no session was removed.
     */
    public Session removeManagedSession(Session s){
        final Session session = managedSessions.remove(s.getId());
        if(session instanceof ManagedSession){
            final ManagedSession ms = (ManagedSession) session;
            final MBeanOperationInfo[] ops = ms.getAllParameters(s.getId()+":");
            for(int i=0; i<ops.length; i++){
                operations.remove(ops[i].getName());
                mboi.remove(ops[i]);
            }
        }
        return session;
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
        
        MBeanOperationInfo[] opsArray = new MBeanOperationInfo[mboi.size()];
        int i=0;
        for(MBeanOperationInfo inf : mboi)
            opsArray[i++] = inf;
        return new MBeanInfo(this.getClass().getName(),
                "Exported operations list",
                null, // attributes
                null, // constructors
                opsArray, // operations
                null); // notifications
    }

    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        Operation op = operations.get(actionName);
        if (op == null)
            return null;
        else{
            try {
                return op.session.invoke(actionName,op.operation,params, signature);
            } catch (AppiaManagementException e) {
                throw new MBeanException(e);
            }
        }
    }

    public void setAttribute(Attribute arg0) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        // TODO Auto-generated method stub
        
    }

    public AttributeList setAttributes(AttributeList arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
