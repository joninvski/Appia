package net.sf.appia.adaptationmanager;

import java.io.Serializable;

import net.sf.appia.core.Layer;

/**
 * 
 * This class defines a Action. 
 * An action is the basic instruction that AdaptationManager gives to the reconfigurator in order to perform 
 * a change in the communication stack. 
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class Action implements Serializable {
    
    private String actionId;
    private String name;
    private Class objectType;
    private String objectId;
    private Object[] parameters;
    
    /**
     * 
     * Creates a new Action.
     */
    public Action(){
        super();
    }
    
    /**
     * 
     * Creates a new Action.
     * @param name the name of the action
     * @param objectType the type of object in which the action will be applied
     * @param string the id of the object in which the action will be applied
     */
    public Action(String name, Class objectType, String string){
        super();
        this.setName(name);
        this.setObjectType(objectType);
        this.setObjectId(string);
    }
    
    /**
     * 
     * Creates a new Action.
     * @param name
     * @param objectType
     * @param string
     * @param parameters
     */
    public Action(String name, Class objectType, String string, Object[] parameters){
        super();
        this.setName(name);
        this.setObjectType(objectType);
        this.setObjectId(string);
        this.parameters = parameters;
    }
    
    /**
     * 
     * Creates a new Action.
     * @param id
     * @param name
     * @param objectType
     * @param string
     * @param parameters
     */
    public Action(String id, String name, Class objectType, String string, Object[] parameters){
        super();
        this.actionId = id;
        this.setName(name);
        this.setObjectType(objectType);
        this.setObjectId(string);
        this.parameters = parameters;
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
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param objectType The objectType to set.
     */
    public void setObjectType(Class objectType) {
        this.objectType = objectType;
    }

    /**
     * @return Returns the objectType.
     */
    public Class getObjectType() {
        return objectType;
    }

    /**
     * @param objectId The objectId to set.
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * @return Returns the objectId.
     */
    public String getObjectId() {
        return objectId;
    }
    
}
