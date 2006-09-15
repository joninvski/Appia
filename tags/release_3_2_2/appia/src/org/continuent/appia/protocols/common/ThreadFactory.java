package org.continuent.appia.protocols.common;

public interface ThreadFactory {

	public Thread newThread(Runnable runnable);
	
	public Thread newThread(Runnable runnable, String name);

	public Thread newThread(Runnable runnable, String name, boolean isDaemon);

}
