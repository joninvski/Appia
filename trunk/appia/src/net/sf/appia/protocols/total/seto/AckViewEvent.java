package net.sf.appia.protocols.total.seto;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.group.Group;
import net.sf.appia.protocols.group.ViewID;
import net.sf.appia.protocols.group.events.GroupSendableEvent;
import net.sf.appia.protocols.group.events.Send;

public class AckViewEvent extends GroupSendableEvent {

    public AckViewEvent() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AckViewEvent(Channel channel, int dir, Session source, Group group, ViewID view_id, Message omsg) throws AppiaEventException {
        super(channel, dir, source, group, view_id, omsg);
        // TODO Auto-generated constructor stub
    }

    public AckViewEvent(Channel channel, int dir, Session source, Group group, ViewID view_id) throws AppiaEventException {
        super(channel, dir, source, group, view_id);
        // TODO Auto-generated constructor stub
    }

    public AckViewEvent(Message omsg) {
        super(omsg);
        // TODO Auto-generated constructor stub
    }

}
