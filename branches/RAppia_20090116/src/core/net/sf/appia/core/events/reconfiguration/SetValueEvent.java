package net.sf.appia.core.events.reconfiguration;

import net.sf.appia.core.reconfigurator.utils.Where;
import net.sf.appia.core.type.ServiceType;

public class SetValueEvent extends RecActionEvent {

     public SetValueEvent(){
        where = new Where();
    }

    /**
     * @param actionObjectType The t to set.
     */
    public void setServiceType(int whereis, String channelId, Class actionObjectType) {
        where.setServiceType(actionObjectType);
        where.setChannelID(channelId);
        where.setWhere(whereis);
    }

    
}
