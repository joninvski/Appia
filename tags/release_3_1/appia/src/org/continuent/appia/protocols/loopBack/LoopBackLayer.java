/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2006 University of Lisbon
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
 package org.continuent.appia.protocols.loopBack;

import org.continuent.appia.core.*;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;


/**
 * LoopBackLayer is the Layer that sends back a copy of the 
 * GroupSendableEvents received from the upper Layers.
 * This is intedende to simulate Broadcast funcionality and
 * it's useful for implementing Atomic Broadcast protocols.<p>
 * This Layer should be placed above the group communication layers
 * and doesn't provide uniform guarantees.
 * This protocol accepts the following events:<p>
 * <ul>
 * <li>GroupSendableEvent
 * <li>ChannelInit
 * </ul>
 * @see org.continuent.appia.protocols.group.events.GroupSendableEvent
 * @see org.continuent.appia.core.events.channel.ChannelInit
 */
public class LoopBackLayer extends Layer {

    /**
     * A simple constructor.
     */
    public LoopBackLayer() {

    	// Events that the protocol needs.
    	evRequire=new Class[1];
    	evRequire[0]=GroupSendableEvent.class;
    	
    	// Events that the protocol accepts.
    	evAccept=new Class[3];
    	evAccept[0]=evRequire[0];
    	evAccept[1]=ChannelInit.class;
    	evAccept[2]=View.class;
    	
    	// Events provided by this layer
    	evProvide=new Class[1];
	    evProvide[0]=evRequire[0];
    }

    /**
     * Standard session instantiation
     */
    public Session createSession() {
        return new LoopBackSession(this);
    }

}
    

    
	
