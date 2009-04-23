package net.sf.appia.core;

import java.util.Set;

import net.sf.appia.core.reconfigurator.utils.ValuedAttribute;
import net.sf.appia.core.type.ServiceType;

public abstract class ReconfigurableSession extends Session{

    //protected ReconfigurableLayer layer;
    
    public ReconfigurableSession(ReconfigurableLayer layer) {
        super(layer);
        // TODO Auto-generated constructor stub
    }
   
    public ReconfigurableLayer getLayer() {
        return (ReconfigurableLayer) layer;
    }
    
    public ServiceType type() {
        return null;
    }

    public void setState(Set<ValuedAttribute> v) {
    }

    public Set<ValuedAttribute> getState() {
        return null;
    }

    public void start(Channel c) {
    }

    public void stop(Channel c) {
    }

    public void resume(Channel c) {
    }

    public void becomeQuiscent() {
    }
}
