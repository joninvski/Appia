package net.sf.appia.protocols.reconfigurator;

import net.sf.appia.adaptationmanager.AdaptationEvent;
import net.sf.appia.adaptationmanager.events.ContextAnswerEvent;
import net.sf.appia.adaptationmanager.events.ContextQueryEvent;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.events.reconfiguration.RecActionEvent;
import net.sf.appia.core.events.reconfiguration.RecActionResponseEvent;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import net.sf.appia.test.xml.ecco.MyEccoEvent;

/**
 * 
 * This class defines a ReconfiguratorLayer
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class ReconfiguratorLayer extends Layer{

    /**
     * Creates a new ReconfiguratorLayer.
     */
    public ReconfiguratorLayer(){

        evRequire = new Class[]{
                ChannelInit.class,
        };

        evProvide = new Class[] {
                RegisterSocketEvent.class,   
                ChannelInit.class,
                RecActionEvent.class,
        };

        evAccept = new Class[]{
                ChannelInit.class,
                ChannelClose.class,
                RegisterSocketEvent.class,
                AdaptationEvent.class,
                MyEccoEvent.class,
                SendableEvent.class,
                RecActionEvent.class,
                RecActionResponseEvent.class,
                ContextAnswerEvent.class,
                ContextQueryEvent.class,
        };
    }

    /**
     * @see net.sf.appia.core.Layer#createSession()
     */
    public Session createSession() {
        return new ReconfiguratorSession(this);
    }
    

}
