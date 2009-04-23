package net.sf.appia.core.events.reconfiguration;

import net.sf.appia.core.events.channel.ChannelEvent;
import net.sf.appia.core.message.Message;
import net.sf.appia.core.reconfigurator.utils.Where;


public class RecActionEvent extends ChannelEvent{
   
    protected String actionId;
    protected String reconfigId;
    protected String reconfigName;
    protected Object[] parameters;
    protected Where where;
     
    /**
     * Creates a new AdaptationEvent.
     */    
    public RecActionEvent(){
        super();
    }
    
     /**
     * Creates a new AdaptationEvent.
     * @param msg
     */
    public RecActionEvent(Message msg) {
        super();
    }

  

    /**
     * @param where The where to set.
     */
    public void setWhere(Where where) {
        this.where = where;
    }

    /**
     * @return Returns the where.
     */
    public Where getWhere() {
        return where;
    }

    /**
     * @param parameters The parameters to set.
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    /**
     * @return Returns the parameters.
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * @param reconfigName The reconfigName to set.
     */
    public void setReconfigName(String reconfigName) {
        this.reconfigName = reconfigName;
    }

    /**
     * @return Returns the reconfigName.
     */
    public String getReconfigName() {
        return reconfigName;
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
    
    
}
