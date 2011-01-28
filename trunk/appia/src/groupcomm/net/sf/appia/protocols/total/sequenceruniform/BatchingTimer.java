package net.sf.appia.protocols.total.sequenceruniform;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.AppiaException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.PeriodicTimer;

public class BatchingTimer extends PeriodicTimer {

    public BatchingTimer(long period, Channel channel, int dir,
            Session source, int qualifier) throws AppiaEventException,
            AppiaException {
        super("batching uniform timer", period, channel, dir, source, qualifier);
    }

}
