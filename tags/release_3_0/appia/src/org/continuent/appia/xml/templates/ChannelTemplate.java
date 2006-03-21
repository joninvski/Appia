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
 * Created on Mar 11, 2004
 *
 */
package org.continuent.appia.xml.templates;

import java.util.Hashtable;
import java.util.LinkedList;

import org.continuent.appia.core.AppiaException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.ChannelCursor;
import org.continuent.appia.core.EventScheduler;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.QoS;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.memoryManager.MemoryManager;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.ChannelProperties;
import org.continuent.appia.xml.utils.SessionProperties;
import org.continuent.appia.xml.utils.SharingState;


/**
 * This class implements a channel template. It is used to generate one or more
 * channels that have identical QoS.
 * 
 * @author Jose Mocito
 *
 */
public class ChannelTemplate {
	
		// Template name
		private String name;
		// Session templates
		private LinkedList sessionTemplates;
		
		/**
		 * Builds a channel template.
		 * 
		 * @param name the template's name.
		 */
		public ChannelTemplate(String name) {
			this.name = name;
			sessionTemplates = new LinkedList();
		}
		
		/**
		 * @return Returns the name.
		 */
		public String getName() {
			return name;
		}
				
		/**
		 * @return Returns the sessionTemplates.
		 */
		public LinkedList getSessionTemplates() {
			return sessionTemplates;
		}
		
		/**
		 * Adds a session to the channel template.
		 * <p>
		 * First session added corresponds to the bottom most layer.
		 * 
		 * @param name the name of the session.
		 * @param sharing the sharing property of the session.
		 * @param layer the layer associated with the session.
		 * @see SharingState
		 */
		public void addSession(String name, int sharing, Layer layer) {
			sessionTemplates.add(new SessionTemplate(name,sharing,layer));
		}
		
		/**
		 * <p>Returns the number of layers defined in this template.</p>
		 * 
		 * @return the number of layers defined in this template.
		 */
		public int numberOfLayers() {
			return sessionTemplates.size();
		}

		/**
		 * Creates a channel.
		 * <p>
		 * Channel returned is not initialized!
		 * 
		 * @param name the name of the channel.
		 * @param label the label of the channel or null if none is defined.
		 * @param params the parameters passed to the channel.
		 * @param globalSessions Hashtable containing the 
		 * 	shared "global sessions".
		 * @param labelSessions Hashtable containing the
		 * 	shared "label sessions".
		 * @return the channel created.
		 * @throws AppiaException
		 */
		/*public Channel createChannel(
				String name,
				String label,
				ChannelProperties params,
				Hashtable globalSessions,
				Hashtable labelSessions)
		throws AppiaException {
			return createChannel(name,label,params,globalSessions,labelSessions,null);
		}*/
		
		/**
		 * Creates a channel.
		 * <p>
		 * Channel returned is not initialized!
		 * 
		 * @param name the name of the channel.
		 * @param label the label of the channel or null if none is defined.
		 * @param params the parameters passed to the channel.
		 * @param globalSessions Hashtable containing the 
		 * 	shared "global sessions".
		 * @param labelSessions Hashtable containing the
		 * 	shared "label sessions".
		 * @param eventScheduler the EventScheduler associated with the channel.
		 * @return the channel created.
		 * @throws AppiaException
		 * 
		 * TODO: criar mmm
		 */
		public Channel createChannel(
				String name,
				String label,
				ChannelProperties params,
				Hashtable globalSessions,
				Hashtable labelSessions,
				EventScheduler eventScheduler, MemoryManager memoryManager) 
		throws AppiaException {
			// Complete name is equal to the given name plus the template name
			//String completeName = name + " " + this.name;
			int numberOfSessions = sessionTemplates.size();
			Layer[] qos_list = new Layer[numberOfSessions];
			SessionTemplate currSession = null;
			// Generates the QoS
			for (int i = 0; i < numberOfSessions; i++) {
				currSession = (SessionTemplate) sessionTemplates.get(i);
				qos_list[i] = currSession.layerInstance();
			}
			QoS qos = null;
			qos = new QoS(name+" QoS",qos_list);
			// Creates the channel based on the QoS
			Channel channel;
			if (eventScheduler == null && memoryManager == null)
				channel = qos.createUnboundChannel(name);
			else if (eventScheduler == null && memoryManager != null)
				channel = qos.createUnboundChannel(name,memoryManager);
			else if (eventScheduler != null && memoryManager == null)
				channel = qos.createUnboundChannel(name,eventScheduler);
			else
				channel = qos.createUnboundChannel(name,eventScheduler,memoryManager);
			ChannelCursor cc = channel.getCursor();
			cc.bottom();
			// Associates the sessions to their corresponding layers
			for (int i = 0; i < numberOfSessions; i++) {
				currSession = (SessionTemplate) sessionTemplates.get(i);
				Session sessionInstance = null;
				// if "global session" then use only global sessions table.
				if (currSession.getSharingState() == SharingState.GLOBAL)
					sessionInstance = currSession.sessionInstance(label,globalSessions);
				// else (is label or private) use only label sessions table.
				else
					sessionInstance = currSession.sessionInstance(label,labelSessions);
				// Verifies if the session accepts parameters and if
				// they are present in the configuration passes them to 
				// the session.
				if (sessionInstance instanceof InitializableSession &&
						params != null &&
						params.containsKey(currSession.getName())) {
					SessionProperties parameters =
						params.getParams(currSession.getName());
					((InitializableSession)sessionInstance).init(parameters);
				}
				cc.setSession(sessionInstance);
				cc.up();
			}
			return channel;
		}
		
		/**
		 * <b>FOR TESTING PURPOSES ONLY!</b>
		 */
		public void printChannelTemplate() {
			Object [] staux = sessionTemplates.toArray();
			SessionTemplate[] st = new SessionTemplate[staux.length];
			for (int i = 0; i < staux.length; i++)
				st[i] = (SessionTemplate) staux[i];
			
			System.out.println("Template Name: "+name);
			for (int i = 0; i < sessionTemplates.size(); i++)
				st[i].printSessionTemplate();
		}
}