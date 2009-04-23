package net.sf.appia.core.events.reconfiguration;

import net.sf.appia.core.events.channel.ChannelEvent;
import net.sf.appia.core.reconfigurator.utils.Where;
import net.sf.appia.core.type.ServiceType;

/**
 * 
 * This class defines a ReconfigurationResponseEvent
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class RecActionResponseEvent extends ChannelEvent {

    private String actionId;
    private String reconfigId;
    private Object returnType;
    
    public RecActionResponseEvent(){
        super();
    }
    
    /**
     * @param actionId The actionId to set.
     */
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }
    /**
     * @return Returns the actionId.
     */
    public String getActionId() {
        return actionId;
    }
    /**
     * @param reconfigId The reconfigId to set.
     */
    public void setReconfigId(String reconfigId) {
        this.reconfigId = reconfigId;
    }
    /**
     * @return Returns the reconfigId.
     */
    public String getReconfigId() {
        return reconfigId;
    }
    /**
     * @param returnType The returnType to set.
     */
    public void setReturnType(Object returnType) {
        this.returnType = returnType;
    }
    /**
     * @return Returns the returnType.
     */
    public Object getReturnType() {
        return returnType;
    }
  
    
}
