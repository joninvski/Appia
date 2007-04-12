package org.continuent.appia.protocols.group.primary;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.group.Group;
import org.continuent.appia.protocols.group.ViewID;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.events.Send;

public class KickEvent extends GroupSendableEvent implements Send {

    public KickEvent(Channel channel, int dir, Session source, Group group,
            ViewID view_id) throws AppiaEventException {
        super(channel, dir, source, group, view_id);
        // TODO Auto-generated constructor stub
    }

    public KickEvent() {
        // TODO Auto-generated constructor stub
    }

    public KickEvent(Channel channel, int dir, Session source, Group group,
            ViewID view_id, Message omsg) throws AppiaEventException {
        super(channel, dir, source, group, view_id, omsg);
        // TODO Auto-generated constructor stub
    }

    public KickEvent(Message omsg) {
        super(omsg);
        // TODO Auto-generated constructor stub
    }

}
