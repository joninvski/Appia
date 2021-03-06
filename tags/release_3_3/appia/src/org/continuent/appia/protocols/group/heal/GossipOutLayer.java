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

package org.continuent.appia.protocols.group.heal;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;

/**
 * The GossipOutLayer is the bridge between the main group communication
 * channel and the specialized channel used to communicate with the Gossip
 * Server.
 * <br>
 * GossipOutEvents from one channel are switched to the other, and vice-versa.
 *
 * @author Alexandre Pinto
 * @version 0.1
 * @see org.continuent.appia.protocols.group.heal.GossipOutEvent
 * @see org.continuent.appia.protocols.group.heal.HealLayer
 * @see org.continuent.appia.gossip.GossipServer
 */
public class GossipOutLayer extends Layer {

    /**
     * Creates a GossipOutLayer.
     *<br>
     *
     * <b>Events Provided</b><br>
     * <ul>
     * <li>appia.protocols.group.heal.GossipOutEvent
     * <li>appia.protocols.udpsimple.RegisterSocketEvent
     * <li>appia.events.channel.Debug
     * </ul>
     *
     * <b>Events Required</b><br>
     * <i>none</i><br>
     *
     * <b>Events Accepted</b>
     * <ul>
     * <li>appia.protocols.group.heal.GossipOutEvent
     * <li>appia.events.channel.ChannelInit
     * <li>appia.events.channel.ChannelClose
     * <li>appia.protocols.group.intra.View
     * <li>appia.events.channel.Debug
     * <li>appia.protocols.group.events.GroupInit
     * <li>appia.protocols.fifo.FIFOUndeliveredEvent
     * </ul>
     */
    public GossipOutLayer() {
        Class init=org.continuent.appia.core.events.channel.ChannelInit.class;
        Class close=org.continuent.appia.core.events.channel.ChannelClose.class;
        Class gossipout=org.continuent.appia.protocols.group.heal.GossipOutEvent.class;
        Class rse=org.continuent.appia.protocols.common.RegisterSocketEvent.class;
        Class view=org.continuent.appia.protocols.group.intra.View.class;
        Class debug=org.continuent.appia.core.events.channel.Debug.class;
        Class groupinit=org.continuent.appia.protocols.group.events.GroupInit.class;
        Class undelivered=org.continuent.appia.protocols.common.FIFOUndeliveredEvent.class;

        evProvide=new Class[] {
                gossipout,
                rse,
                debug,
        };

        evRequire=new Class[] {};

        evAccept=new Class[] {
                gossipout,
                init,
                close,
                view,
                groupinit,
                undelivered,
        };
    }

    /**
     * Creates a new GossipOutSession.
     */
    public Session createSession() {
        return new GossipOutSession(this);
    }
}