package net.sf.appia.core;

import java.util.Set;

import net.sf.appia.core.reconfigurator.utils.ValuedAttribute;
import net.sf.appia.core.type.ServiceType;

public interface Service {

    ServiceType type();
    
    void setState(Set<ValuedAttribute> v);
    
    Set<ValuedAttribute> getState();
    
    void start(Channel c);
    
    void stop(Channel c);
    
    void resume(Channel c);
    
    void becomeQuiscent();
    
}
