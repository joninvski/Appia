
/*
 *
 * APPIA: Protocol composition and execution framework
 * Copyright (C) 2005 Laboratorio de Sistemas Informaticos de Grande Escala (LASIGE)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * Contact
 * 	Address:
 * 		LASIGE, Departamento de Informatica, Bloco C6
 * 		Faculdade de Ciencias, Universidade de Lisboa
 * 		Campo Grande, 1749-016 Lisboa
 * 		Portugal
 * 	Email:
 * 		appia@di.fc.ul.pt
 * 	Web:
 * 		http://appia.di.fc.ul.pt
 * 
 */
 /*
 * Created on 30-Mar-2005
 *
 */
package org.continuent.appia.protocols.total.seto;

import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.ChannelClose;
import org.continuent.appia.core.events.channel.ChannelInit;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.sync.BlockOk;
import org.continuent.appia.protocols.total.common.RegularServiceEvent;
import org.continuent.appia.protocols.total.common.SETOServiceEvent;
import org.continuent.appia.protocols.total.common.UniformServiceEvent;

/**
 * @author nunomrc
 *
 */
public class SETOLayer extends Layer {

	public SETOLayer(){
		super();
		evAccept = new Class[]{
				ChannelInit.class,
				ChannelClose.class,
				GroupSendableEvent.class,
				View.class,
				BlockOk.class,
				SeqOrderEvent.class,
				SETOTimer.class,
				UniformTimer.class,
				UniformInfoEvent.class,
		};
		
		evRequire = new Class[]{};
		
		evProvide = new Class[]{
				UniformServiceEvent.class,
				RegularServiceEvent.class,
				SETOServiceEvent.class,
		};
	}
	
	/* (non-Javadoc)
	 * @see appia.Layer#createSession()
	 */
	public Session createSession() {
		return new SETOSession(this);
	}

}
