package net.sf.appia.project.group.server;

import java.net.InetSocketAddress;
import java.util.List;

import net.sf.appia.project.group.GlobalConfigs;

public class PongManager implements Runnable {

	private VsProxySession session;
	private InetSocketAddress serverAddress;

	public PongManager(VsProxySession session, InetSocketAddress serverAddress){
		this.session = session;
		this.serverAddress = serverAddress;
	}
	
	public void run() {
		while(true){
			try {
				Thread.sleep(GlobalConfigs.PONG_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			VsGroup[] allGroups = VsClientManagement.getAllGroups();

			for (VsGroup group : allGroups) {
				List<VsClient> failedGroupClients = group.updatePongTimes(serverAddress);

				for(VsClient client : failedGroupClients){
					session.considerClientDead(client);
				}
			}
		}
	}
}
