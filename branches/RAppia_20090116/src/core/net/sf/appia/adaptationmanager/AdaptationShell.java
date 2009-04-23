package net.sf.appia.adaptationmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;

/**
 * 
 * This class defines a AdaptationShell. The shell is only for testing the manager.
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class AdaptationShell implements Runnable {

private Channel channel;
    
    /**
     * Creates a new AdaptationShell.
     * @param ch
     */
    public AdaptationShell(Channel ch) {
        channel = ch;
    }
    
    /**
     * Execution of the thread.
     * @see java.lang.Runnable#run()
     */
    public void run() {
        boolean dontExit = true;
        while(dontExit) {
            System.out.print("> ");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            String str = "";
            try {
                str = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            AdaptationEvent event = new AdaptationEvent();
            try {
                event.asyncGo(channel,Direction.DOWN);
            } catch (AppiaEventException e1) {
                e1.printStackTrace();
            }
        }
    }
    
}
