package net.sf.appia.project.group.client;

import net.sf.appia.project.group.GlobalConfigs;


public class PongSender implements Runnable {

	private VsStubSession session;
	
	/**
     * Creates a new MyShell.
     * @param ch
     */
    public PongSender(VsStubSession session) {
        this.session = session;
    }

	
	public void run() {
		while(true){
			try {
				Thread.sleep(GlobalConfigs.PONG_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			session.sendPong();
		}
	}
}
