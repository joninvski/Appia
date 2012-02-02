package net.sf.appia.project.group.event.proxy;

import net.sf.appia.protocols.group.events.GroupSendableEvent;

/**
 * The proxy event is used to send messages between servers
 * 
 * @author jtrindade
 */
public abstract class ProxyEvent extends GroupSendableEvent{

	public ProxyEvent(){
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

