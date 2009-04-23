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
package net.sf.appia.test.xml.ecco;

import java.util.HashSet;
import java.util.Set;

import net.sf.appia.core.Layer;
import net.sf.appia.core.ReconfigurableLayer;
import net.sf.appia.core.ReconfigurableSession;
import net.sf.appia.core.Session;
import net.sf.appia.core.TimeProvider;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.reconfigurator.utils.Attribute;
import net.sf.appia.core.type.ServiceType;
import net.sf.appia.core.type.TransportServiceType;
import net.sf.appia.protocols.common.RegisterSocketEvent;


/**
 * This class defines a EccoLayer
 * 
 * @author Jose Mocito
 * @version 1.0
 */
public class EccoLayer extends ReconfigurableLayer{

    /**
     * Creates a new EccoLayer.
     */
	public EccoLayer() {
		
	    
	    //attributes from layer
		evRequire = new Class[]{
		        ChannelInit.class,
		};
        
		evProvide = new Class[] {
          RegisterSocketEvent.class,      
        };
		
		evAccept = new Class[]{
                ChannelInit.class,
                ChannelClose.class,
                RegisterSocketEvent.class,
                MyEccoEvent.class,
        };
		
		//?? para quê? validação?
	    // subtypes = new HashSet<ServiceType>();

	    // quando validar ver se é instanceof ServiceType
	    requiredServiceTypes = new Class[]{
	            TransportServiceType.class
	    };

		addaptableParams = new Attribute[]{
		        new Attribute("localPort", int.class),
		};
		
		// relacionado com o monitor de contexto
		//contextTraps = new HashSet<Attribute>();
		//contextQueries = new HashSet<Attribute>();

		transferableState = new Attribute[]{
		        new Attribute("time", TimeProvider.class),
		};

		
	}
	
	/**
     * Creates the session for this protocol.
	 * @see Layer#createSession()
	 */
	public ReconfigurableSession createSession() {
		return new EccoSession(this);
	}
}
