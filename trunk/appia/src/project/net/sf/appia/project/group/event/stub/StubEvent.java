package net.sf.appia.project.group.event.stub;

import net.sf.appia.core.events.SendableEvent;

/*
 * The Stub event class is used to send messages between 
 * clients and the servers
 */
public abstract class StubEvent extends SendableEvent{

	public StubEvent(){
		super();
	}

	/*
	 * Warning, only use one time this method
	 */
	public abstract void storeMessage();

	/*
	 * Warning, only use one time this method
	 */
	public abstract void loadMessage();
}

