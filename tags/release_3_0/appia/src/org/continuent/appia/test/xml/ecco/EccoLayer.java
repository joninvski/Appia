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
 /*
 * Created on Mar 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.continuent.appia.test.xml.ecco;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;

/**
 * @author jmocito
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class EccoLayer extends Layer {
	
	public EccoLayer() {
		
		Class crse=org.continuent.appia.protocols.common.RegisterSocketEvent.class;
		Class cinit=org.continuent.appia.core.events.channel.ChannelInit.class;
		Class cclose=org.continuent.appia.core.events.channel.ChannelClose.class;
		Class cecho=org.continuent.appia.test.xml.ecco.MyEccoEvent.class;
		
		evRequire = new Class[1];
		evRequire[0] = cinit;
		
		evProvide = new Class[1];
		evProvide[0] = crse;
		
		evAccept = new Class[3];
		evAccept[0] = cinit;
		evAccept[1] = cclose;
		evAccept[2] = cecho;
	}
	
	/* (non-Javadoc)
	 * @see appia.Layer#createSession()
	 */
	public Session createSession() {
		// TODO Auto-generated method stub
		return new EccoSession(this);
	}
}
