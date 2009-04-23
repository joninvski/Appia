package net.sf.appia.core.reconfigurator;

import net.sf.appia.core.reconfigurator.utils.How;


public interface ReconfigureChannel{

    void becomeQuiscent(String ChannelID);
    
    void resumeChannel(String ChannelID);
    
    void startChannel(String channelID, How o);
    
    void stopChannel(String channelID, How o);   
}
