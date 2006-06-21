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

package org.continuent.appia.management.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.AppiaException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.ChannelCursor;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Session;
import org.continuent.appia.management.ManagedSessionEvent;
import org.continuent.appia.management.SensorSession;
import org.continuent.appia.management.SensorSessionListener;
import org.continuent.appia.xml.utils.SessionProperties;

/**
 * @author nuno
 *
 */
public class ChannelManager extends NotificationBroadcasterSupport 
implements ChannelManagerMBean, SensorSessionListener {
	
	private Channel channel;
	
	public ChannelManager(Channel ch){
		channel = ch;
	}
	
	public void registerMBean() throws AppiaException{
		System.out.println("Starting JMX...");
		// Get the Platform MBean Server
		MBeanServer mbs = MBeanServerFactory.createMBeanServer();
		
		// Construct the ObjectName for the MBean we will register
		ObjectName name;
		try {
			name = new ObjectName("appia.mbeans:type=ChannelManager "+channel.getChannelID());
			System.out.println("MBean Object name created.");
		} catch (MalformedObjectNameException e) {
			throw new AppiaException("Could not create MBean Object: "+e.getMessage());
		} catch (NullPointerException e) {
			throw new AppiaException("Could not create MBean Object: "+e.getMessage());
		}
		
		// Register it self
		try {
			mbs.registerMBean(this, name);
			System.out.println("MBean registered in server.");
		} catch (InstanceAlreadyExistsException e) {
			throw new AppiaException("Could not register MBean: "+e.getMessage());
		} catch (MBeanRegistrationException e) {
			throw new AppiaException("Could not register MBean: "+e.getMessage());
		} catch (NotCompliantMBeanException e) {
			throw new AppiaException("Could not register MBean: "+e.getMessage());
		}
		
		ChannelCursor cc = channel.getCursor();
		cc.top();
		while(cc.isPositioned()){
			System.out.println("Session: "+cc.getSession());

			Session session = cc.getSession();
			if(session instanceof SensorSession){
				SensorSession ss = (SensorSession) session;
				ss.addSensorListener(this);
			}
			cc.down();
		}
	}

	public void unregisterMBean() throws AppiaException{
		// Get the Platform MBean Server
		MBeanServer mbs = MBeanServerFactory.createMBeanServer();
		
		// Construct the ObjectName for the MBean we will register
		ObjectName name;
		try {
			name = new ObjectName("appia.mbeans:type=ChannelManager "+channel.getChannelID());
		} catch (MalformedObjectNameException e) {
			throw new AppiaException("Could not create MBean Object: "+e.getMessage());
		} catch (NullPointerException e) {
			throw new AppiaException("Could not create MBean Object: "+e.getMessage());
		}
		
		try {
			mbs.unregisterMBean(name);			
			System.out.println("MBean unRegistered from server.");
		} catch (MBeanRegistrationException e) {
			throw new AppiaException("Could not unregister MBean: "+e.getMessage());
		} catch (InstanceNotFoundException e) {
			throw new AppiaException("MBean not found: "+e.getMessage());
		}
		
		ChannelCursor cc = channel.getCursor();
		cc.top();
		while(cc.isPositioned()){
			System.out.println("Session: "+cc.getSession());

			Session session = cc.getSession();
			if(session instanceof SensorSession){
				SensorSession ss = (SensorSession) session;
				ss.removeSensorListener(this);
			}
			cc.down();
		}
	}
	
	public void setParameter(String parameter, String value) {
		SessionProperties props = new SessionProperties();
		props.setProperty(parameter,value);
		try {
			ManagedSessionEvent event = new ManagedSessionEvent(props);
			event.asyncGo(channel,Direction.DOWN);
		} catch (AppiaEventException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onNotification(Notification notification) {
		notification.setSource(this);
		sendNotification(notification);
	}

	public String getChannelName() {
		return channel.getChannelID();
	}

	public String getParameter(String parameter) {
		// TODO Auto-generated method stub
		return "TODO";
	}

}
