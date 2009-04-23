package net.sf.appia.core.reconfigurator.utils;

import net.sf.appia.core.Layer;
import net.sf.appia.core.type.ServiceType;

//final??
public class Where {

    public static final int ABOVE = +1;
    public static final int BELOW = -1;
    
    private String channelID;
    private int where;
    private Class serviceType;
    
    public Where(){
        super();
    }
    
    public Where(int where, String id, Class st){
        super();
        
        this.where= where;
        this.channelID = id;
        this.serviceType = st;
    }
    
    public String getChannelID(){
        return channelID;
    }
    
    public void setChannelID(String id){
        this.channelID = id;
    }
    
    public int getWhere(){
        return where;
    }
    
    public void setWhere(int where){
        this.where = where;
    }
 
    public Class getServiceType(){
        return serviceType;
    }
    
    public void setServiceType(Class actionObjectType){
        this.serviceType = actionObjectType;
    }
    
}
