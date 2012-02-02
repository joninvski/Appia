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
package net.sf.appia.project.debug.counter;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelClose;


/**
 * This class defines a Layer that counts the messages that go up and down.
 * 
 * @author Joao Trindade
 * @version 1.0
 */
public class CounterSession extends Session {

	private int counterUp = 0;
	private int counterDown = 0;

	/**
	 * Creates a new EccoSession.
	 * @param l
	 */
	public CounterSession(CounterLayer l) {
		super(l);
	}

	/**
	 * Main event handler.
	 * @param ev the event to handle.
	 * 
	 * @see net.sf.appia.core.Session#handle(net.sf.appia.core.Event)
	 */
	public void handle(Event ev) {
		System.out.println("Session Counter - "+ ev.getDir()
				+  " - Received an event type - " + ev.getClass());

		if (ev instanceof ChannelClose)
			handleChannelClose((ChannelClose) ev);

		else{ // Anything other type of event besides close
			handleEvent((SendableEvent) ev);
		}
	}

	/*
	 * Handles any event
	 */
	private void handleEvent(SendableEvent ev) {
		if (ev.getDir() == Direction.DOWN) {
			counterDown++;
		} 
		else { // Event is going UP
			counterUp++;
		}
		try {ev.go();} 
		catch (AppiaEventException e) {	e.printStackTrace();}

		System.out.print("CounterUp: " + counterUp);		
		System.out.print("\tDownCounter: " + counterDown);
		
		int total = counterUp + counterDown;
		System.out.println("\tTotal: " + total);
	}

	/*
	 * ChannelClose
	 */
	private void handleChannelClose(ChannelClose close) {
		try {
			System.out.println("Counter channel closed");
			close.go();
		} catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}
}
