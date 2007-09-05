/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2006-2007 University of Lisbon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Alexandre Pinto and Hugo Miranda.
 * Contributor(s): See Appia web page for a list of contributors.
 */

package net.sf.appia.protocols.measures.throughput;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.AppiaException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.EventQualifier;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.TimeProvider;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.management.AppiaManagementException;
import net.sf.appia.management.ManagedSession;

/**
 * This class defines a ThroughputSession
 * 
 * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
 * @version 1.0
 */
public class ThroughputSession extends Session implements ManagedSession {

    private static Logger log = Logger.getLogger(ThroughputSession.class);
    
    public static final String THRPUT_MSG_PER_SECOND_UP = "msg_per_second_up";
    public static final String THRPUT_MSG_PER_SECOND_DOWN = "msg_per_second_down";
    public static final String THRPUT_BYTES_PER_SECOND_UP = "bytes_per_second_up";
    public static final String THRPUT_BYTES_PER_SECOND_DOWN = "bytes_per_second_down";
    
    private Throughput msgPerSecondUp, msgPerSecondDown, bytesPerSecondUp, bytesPerSecondDown;
    private boolean created = false;
    private List<Channel> channels = new ArrayList<Channel>();
    private Channel timerChannel = null;

    /**
     * This class defines a Throughput.
     * 
     * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
     * @version 1.0
     */
    class Throughput {
        private static final long SECOND=1000000;
        private TimeProvider time = null;
        private long sum=0, initialTime=0;
        
        Throughput(TimeProvider time){
            this.time = time;
            initialTime = time.currentTimeMicros();
        }
        
        long get(){
            long t = ((time.currentTimeMicros()-initialTime)/SECOND);
            if(t == 0)
                t = 1;
            return sum/t;
        }
        
        void add(long value){
            sum += value;
        }
        
        @Override
        public String toString(){
            return ""+get();
        }
    }
    
    /**
     * Creates a new ThroughputSession.
     * @param layer
     */
    public ThroughputSession(Layer layer) {
        super(layer);
    }
    
    /**
     * 
     * 
     * @see net.sf.appia.core.Session#handle(net.sf.appia.core.Event)
     */
    public void handle(Event event){
        if(event instanceof SendableEvent)
            handleSendable((SendableEvent)event);
        else if(event instanceof ChannelInit)
            handleChannelInit((ChannelInit)event);
        else if(event instanceof ChannelClose)
            handleChannelClose((ChannelClose)event);
        else if(event instanceof ThroughputDebugTimer)
            handleDebugTimer();
        else
            try {
                log.debug("Forwarding unwanted event: "+event.getClass().getName());
                event.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
    }

    private void handleDebugTimer() {
        log.debug("Throughput going DOWN: Messages per second = "
                +msgPerSecondDown+" and bytes per second = "+bytesPerSecondDown);
        log.debug("Throughput going UP: Messages per second = "
                +msgPerSecondUp+" and bytes per second = "+bytesPerSecondUp);
    }

    private void handleChannelClose(ChannelClose close) {
        final Channel ch = close.getChannel();
        channels.remove(ch);
        
        if(log.isDebugEnabled()){
            if(timerChannel != null && timerChannel == ch){
                try {
                    new ThroughputDebugTimer(timerChannel,this,EventQualifier.OFF).go();
                    if(channels.size()>0){
                        timerChannel = channels.get(0);
                        new ThroughputDebugTimer(timerChannel,this,EventQualifier.ON).go();
                    }
                    else
                        timerChannel = null;
                } catch (AppiaEventException e) {
                    e.printStackTrace();
                } catch (AppiaException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            close.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }

    private void handleChannelInit(ChannelInit init) {
        final TimeProvider tp = init.getChannel().getTimeProvider();
        channels.add(init.getChannel());
        if(!created){
            msgPerSecondUp = new Throughput(tp);
            msgPerSecondDown = new Throughput(tp);
            bytesPerSecondUp = new Throughput(tp);
            bytesPerSecondDown = new Throughput(tp);
            if(log.isDebugEnabled()){
                try {
                    new ThroughputDebugTimer(init.getChannel(),this,EventQualifier.ON).go();
                } catch (AppiaEventException e) {
                    e.printStackTrace();
                } catch (AppiaException e) {
                    e.printStackTrace();
                }
            }
            created = true;
        }

        try {
            init.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }        
    }

    private void handleSendable(SendableEvent event) {
        if(event.getDir() == Direction.DOWN){
            msgPerSecondDown.add(1);
            bytesPerSecondDown.add(event.getMessage().length());
        }
        else{
            msgPerSecondUp.add(1);
            bytesPerSecondUp.add(event.getMessage().length());
        }
        
        try {
            event.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }

    public String getParameter(String parameter) throws AppiaManagementException {
        if(parameter.equals(THRPUT_MSG_PER_SECOND_UP))
            return msgPerSecondUp.toString();
        if(parameter.equals(THRPUT_MSG_PER_SECOND_DOWN))
            return msgPerSecondDown.toString();
        if(parameter.equals(THRPUT_BYTES_PER_SECOND_UP))
            return bytesPerSecondUp.toString();
        if(parameter.equals(THRPUT_BYTES_PER_SECOND_DOWN))
            return bytesPerSecondDown.toString();
        throw new AppiaManagementException("Parameter '"+parameter+"' not defined in session "+this.getClass().getName());
    }

    public void setParameter(String parameter, String value) throws AppiaManagementException {
        throw new AppiaManagementException("The Session "+this.getClass().getName()+" does not accept any parameter to "+
                "set a new value. It is read only.");
    }

}
