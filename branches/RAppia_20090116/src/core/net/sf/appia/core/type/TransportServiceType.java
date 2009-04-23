package net.sf.appia.core.type;

import net.sf.appia.core.Service;
import net.sf.appia.core.Session;

//? interface??
public class TransportServiceType extends ServiceType {

    Class<Service> concreteImplementation;
    
    public Class<Service> implementation() {
        return concreteImplementation;
    }
    
    @Override
    public Session createSession() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
