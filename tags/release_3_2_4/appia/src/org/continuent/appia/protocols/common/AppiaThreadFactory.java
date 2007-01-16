package org.continuent.appia.protocols.common;

public class AppiaThreadFactory implements ThreadFactory {

	private static ThreadFactory factory = null;
	private AppiaThreadFactory() {}

	public static ThreadFactory getThreadFactory(){
		if(factory == null)
			factory = new AppiaThreadFactory();
		return factory;
	}
	
	public Thread newThread(Runnable runnable) {
		return new Thread(runnable);
	}

	public Thread newThread(Runnable runnable, String name) {
		return new Thread(runnable,name);
	}

	public Thread newThread(Runnable runnable, String name, boolean isDaemon) {
		Thread thread = newThread(runnable,name);
		thread.setDaemon(isDaemon);
		return thread;
	}

}
