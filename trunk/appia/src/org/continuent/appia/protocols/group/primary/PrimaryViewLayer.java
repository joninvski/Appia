package org.continuent.appia.protocols.group.primary;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;

public class PrimaryViewLayer extends Layer {

    public PrimaryViewLayer() {
       super();
       
       evAccept = new Class[]{
                View.class,
                BlockOk.class,
                ProbeEvent.class,
                DeliverViewEvent.class,
                KickEvent.class,
        };
        
        evRequire = new Class[]{
                View.class,
                BlockOk.class,
        };
        
        evProvide = new Class[]{
                ProbeEvent.class,
                DeliverViewEvent.class,
                KickEvent.class,
        };
    }

    public Session createSession() {
       return new PrimaryViewSession(this);
    }

}
