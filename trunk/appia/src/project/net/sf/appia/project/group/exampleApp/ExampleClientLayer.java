package net.sf.appia.project.group.exampleApp;

import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import net.sf.appia.protocols.group.events.GroupInit;
import net.sf.appia.protocols.group.events.GroupSendableEvent;
import net.sf.appia.protocols.group.intra.View;
import net.sf.appia.protocols.group.leave.LeaveEvent;
import net.sf.appia.protocols.group.sync.BlockOk;

public class ExampleClientLayer extends Layer {

	public ExampleClientLayer(){
		evRequire = new Class[]{
				ChannelInit.class,
		};

		evProvide = new Class[]{
				GroupInit.class, 
				BlockOk.class,
				LeaveEvent.class,
				GroupSendableEvent.class,
				RegisterSocketEvent.class,
		};

		evAccept = new Class[]{
				ChannelInit.class,
				View.class,
				BlockOk.class,
				RegisterSocketEvent.class,
				TextEvent.class,
		};
	}

	/**
	 * Creates the session for this protocol.
	 * @see Layer#createSession()
	 */
	public Session createSession() {
		return new ExampleClientSession(this);
	}
}