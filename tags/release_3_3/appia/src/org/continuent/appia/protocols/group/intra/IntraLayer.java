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

/**
 * Title:        Apia<p>
 * Description:  Protocol development and composition framework<p>
 * Copyright:    Copyright (c) Alexandre Pinto & Hugo Miranda & Luis Rodrigues<p>
 * Company:      F.C.U.L.<p>
 * @author Alexandre Pinto & Hugo Miranda & Luis Rodrigues
 * @version 1.0
 */
package org.continuent.appia.protocols.group.intra;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.EchoEvent;
import org.continuent.appia.protocols.group.events.GroupInit;
import org.continuent.appia.protocols.group.suspect.Fail;


public class IntraLayer extends Layer {

    public IntraLayer() {
        Class view=org.continuent.appia.protocols.group.intra.View.class;
        Class install=org.continuent.appia.protocols.group.intra.InstallView.class;
        Class init=GroupInit.class;
        Class preview=org.continuent.appia.protocols.group.intra.PreView.class;
        Class newview=org.continuent.appia.protocols.group.intra.NewView.class;

        evProvide=new Class[] {
                view,
                install,
                EchoEvent.class,
                preview,
                newview,
        };

        evRequire=new Class[] {
                init,
        };

        evAccept=new Class[] {
                install,
                Fail.class,
                init,
                view,
                org.continuent.appia.protocols.group.intra.ViewChange.class,
                preview,
                newview,
        };
    }

    public Session createSession() {
        return new IntraSession(this);
    }
}