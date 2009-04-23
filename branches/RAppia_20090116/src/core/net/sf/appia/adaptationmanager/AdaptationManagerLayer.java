package net.sf.appia.adaptationmanager;

import net.sf.appia.adaptationmanager.events.ContextAnswerEvent;
import net.sf.appia.adaptationmanager.events.ContextQueryEvent;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.protocols.common.RegisterSocketEvent;

public class AdaptationManagerLayer extends Layer {

    /**
     * Creates a new AdaptationManagerLayer.
     */
    public AdaptationManagerLayer(){

        evRequire = new Class[]{
                ChannelInit.class,
        };

        evProvide = new Class[] {
                RegisterSocketEvent.class,      
        };

        evAccept = new Class[]{
                ChannelInit.class,
                ChannelClose.class,
                RegisterSocketEvent.class,
                AdaptationEvent.class,
                AdaptationResponseEvent.class,
                ContextAnswerEvent.class,
                ContextQueryEvent.class,
        };
    }

    /**
     * Creates a AdaptationManagerSession
     */
    public Session createSession() {
        return new AdaptationManagerSession(this);
    }
}
