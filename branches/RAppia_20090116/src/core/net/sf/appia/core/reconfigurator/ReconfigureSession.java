package net.sf.appia.core.reconfigurator;

import java.util.Set;

import net.sf.appia.core.reconfigurator.utils.How;
import net.sf.appia.core.reconfigurator.utils.ValuedAttribute;
import net.sf.appia.core.reconfigurator.utils.Where;
import net.sf.appia.core.type.ServiceType;

public interface ReconfigureSession {

    void setValue(Set<ValuedAttribute> v, String channelID);
   
    void addService (ServiceType c, Where h);
    
    void removeService(ServiceType s, Where h);
    
    void startBuffering(Where h);
    
    void stopBuffering(Where h, How o);
  
    void becomeQuiscent(String serviceID, String channelID);
    
    void resumeChannel(String serviceID, String channelID);
    
    void getServiceState(ServiceType s, String channelID);
    
    void setServiceState(Set<ValuedAttribute> v, String channelID);
    
    void startService(String serviceID, String channelID);
    
    void stopService(ServiceType s, String channelID);
    
}
