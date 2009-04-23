package net.sf.appia.core.type;

import java.util.Set;

import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.reconfigurator.utils.Attribute;

/**
 * 
 * This class defines a ServiceType
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public abstract class ServiceType extends Type {

    protected Set<ServiceType> subtypes;
    
    protected Class[] requiredServiceTypes;
       
    protected Attribute[] addaptableParams;
    
    protected Set<Attribute> contextTraps;
    
    protected Set<Attribute> contextQueries;
    
    protected Attribute[] transferableState;

    /**
     * @param subtypes The subtypes to set.
     */
    public void setSubtypes(Set<ServiceType> subtypes) {
        this.subtypes = subtypes;
    }

    /**
     * @return Returns the subtypes.
     */
    public Set<ServiceType> getSubtypes() {
        return subtypes;
    }

    /**
     * @param requiredServiceTypes The requiredServiceTypes to set.
     */
    public void setRequiredServiceTypes(Class[] requiredServiceTypes) {
        this.requiredServiceTypes = requiredServiceTypes;
    }

    /**
     * @return Returns the requiredServiceTypes.
     */
    public Class[] getRequiredServiceTypes() {
        return requiredServiceTypes;
    }

    /**
     * @param addaptableParams The addaptableParams to set.
     */
    public void setAddaptableParams(Attribute[] addaptableParams) {
        this.addaptableParams = addaptableParams;
    }

    /**
     * @return Returns the addaptableParams.
     */
    public Attribute[] getAddaptableParams() {
        return addaptableParams;
    }

    /**
     * @param contextTraps The contextTraps to set.
     */
    public void setContextTraps(Set<Attribute> contextTraps) {
        this.contextTraps = contextTraps;
    }

    /**
     * @return Returns the contextTraps.
     */
    public Set<Attribute> getContextTraps() {
        return contextTraps;
    }

    /**
     * @param contextQueries The contextQueries to set.
     */
    public void setContextQueries(Set<Attribute> contextQueries) {
        this.contextQueries = contextQueries;
    }

    /**
     * @return Returns the contextQueries.
     */
    public Set<Attribute> getContextQueries() {
        return contextQueries;
    }

    /**
     * @param transferableState The transferableState to set.
     */
    public void setTransferableState(Attribute[] transferableState) {
        this.transferableState = transferableState;
    }

    /**
     * @return Returns the transferableState.
     */
    public Attribute[] getTransferableState() {
        return transferableState;
    }

    public Session createSession() {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
