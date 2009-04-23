package net.sf.appia.adaptationmanager;

import java.io.Serializable;

import net.sf.appia.core.Layer;

/**
 * 
 * This class defines a ActionResponse. 
 * An action response represents the result of the corresponding Action operation. 
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class ActionResponse implements Serializable {

    private String actionId;
    private Class returnType;
    private Object returnValue;
    
    /**
     * 
     * Creates a new Action.
     */
    public ActionResponse(){
        super();
    }
    
   /**
    * 
    * Creates a new ActionResponse.
    * @param actionId
    * @param returnValue
    */
    public ActionResponse(String actionId, Object returnValue){
        super();
        this.actionId = actionId;
        this.returnValue = returnValue;
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
     * @param returnType The returnType to set.
     */
    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }

    /**
     * @return Returns the returnType.
     */
    public Object getReturnType() {
        return returnType;
    }

    /**
     * @param returnValue The returnValue to set.
     */
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * @return Returns the returnValue.
     */
    public Object getReturnValue() {
        return returnValue;
    }
    
}
