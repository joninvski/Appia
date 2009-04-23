package net.sf.appia.core.reconfigurator.utils;

/**
 * This class defines a ValuedAttribute which represents an Attribute and 
 * with its value.
 * 
 * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
 * @version 1.0
 */
public class ValuedAttribute{

    private Attribute attribute;
    private Object value;

    /**
     * Creates a new ValuedAttribute.
     * @param attribute
     * @param value
     */
    public ValuedAttribute(Attribute attribute, Object value){

        this.attribute = attribute;

        Class c;

        try {
            c = Class.forName(attribute.getType().getCanonicalName());

           // System.out.println("Att type: " + attribute.getType() + " " + c.isPrimitive());

           // System.out.println(attribute.getType().getCanonicalName());
            if(attribute.getType().getSuperclass().getCanonicalName().equals("java.lang.Number"))
                this.setValue(value);
            else
                c.newInstance() ;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            System.out.println( e.getCause());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param attribute The attribute to set.
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return Returns the attribute.
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }


}
