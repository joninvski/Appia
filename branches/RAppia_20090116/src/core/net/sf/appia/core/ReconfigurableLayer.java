package net.sf.appia.core;

import java.util.Set;

import net.sf.appia.core.reconfigurator.utils.Attribute;
import net.sf.appia.core.type.ServiceType;

public abstract class ReconfigurableLayer extends Layer{

    protected Set<ServiceType> subtypes;
    
    protected Class[] requiredServiceTypes;
    
    //é suposto os eventos também terem um tipo? estas duas funcionalidades são o que actualmente se faz
    // com a layer: evAccept, evProvide e evRequired
   // private Set<EventType> consummedCommEvents;
   // private Set<EventType> producedCommEvents;
    // -> como faz extends da layer não é preciso!!
    
    protected Attribute[] addaptableParams;
    
    protected Set<Attribute> contextTraps;
    
    protected Set<Attribute> contextQueries;
    
    protected Attribute[] transferableState;

    public abstract ReconfigurableSession createSession() ;
    
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
    
    
}
