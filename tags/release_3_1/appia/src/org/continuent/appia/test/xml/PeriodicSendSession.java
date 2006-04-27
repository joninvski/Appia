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
 * Created on 26/Abr/2004
 */
package org.continuent.appia.test.xml;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javax.swing.Timer;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.SessionProperties;


/**
 * XML parameters:
 * 					timeout : int 
 * 
 * @author Nuno Almeida
 */
public class PeriodicSendSession extends Session implements InitializableSession {
	
	private Timer timer;
	private LinkedList events;
	private int timeout;
	
	public PeriodicSendSession(Layer layer) {
		super(layer);
		events = new LinkedList();
		timeout = 10;
		
		timer = new Timer(timeout, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				GroupSendableEvent de = null;	
		    		
		    	try {
		    		de = (GroupSendableEvent) events.removeFirst();
		    	} catch (NoSuchElementException e) {}
		    	
		    	if (de != null) {	
		    		try {
		    			de.go();
		    		} catch (AppiaEventException e) {
		    			e.printStackTrace();
		    		}
		     	}		    	
		    }    
		});

		timer.start();	
	}
	
	/* (non-Javadoc)
	 * @see appia.xml.interfaces.InitializableSession#init(appia.xml.utils.SessionProperties)
	 */
	public void init(SessionProperties params) {
		// TODO Auto-generated method stub
		timeout = params.getInt("timeout");
	}
	
	public void handle(Event ev) {
		
		if (ev instanceof DrawEvent)
			handleAddEvent((DrawEvent)ev);
		else if (ev instanceof MouseButtonEvent)
			handleAddEvent((MouseButtonEvent)ev);
		else if (ev instanceof ClearWhiteBoardEvent)
			handleDeleteEvent((ClearWhiteBoardEvent)ev);
		else
			try {
				ev.go();
			} catch (AppiaEventException ex) {
				ex.printStackTrace();
			}
	}
	
	private void handleAddEvent(DrawEvent event) {
		if(event.getDir() == Direction.DOWN) 
			events.add(event);
		else {		
			try {
				event.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleAddEvent(MouseButtonEvent event) {
		if(event.getDir() == Direction.DOWN) 
			events.add(event);
		else {
			try {
				event.go();
			} catch (AppiaEventException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void handleDeleteEvent(ClearWhiteBoardEvent event) {
			events.clear();
			
			try {
				event.go();
			} catch (AppiaEventException e) {
				e.printStackTrace();
			}
	}
}	
