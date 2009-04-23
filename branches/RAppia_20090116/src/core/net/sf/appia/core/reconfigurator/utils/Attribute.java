package net.sf.appia.core.reconfigurator.utils;

/**
 * 
 * This class defines a Attribute which is a pair with a value and the 
 * class of the value.
 * 
 * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
 * @version 1.0
 */
public class Attribute {

    private String name;
    private Class type;
    
    /**
     * 
     * Creates a new Attribute.
     */
    public Attribute(){
        super();
    }
    
    /**
     * 
     * Creates a new Attribute.
     * @param name
     * @param type
     */
    public Attribute(String name, Class type){
        super();
        this.name = name;
        this.type = type;
    }
    
    /**
     * @param type The type to set.
     */
    public void setType(Class type) {
        this.type = type;
    }
    /**
     * @return Returns the type.
     */
    public Class getType() {
        return type;
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
    
}
