
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
package org.continuent.appia.protocols.total.seto;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.message.Message;
import org.continuent.appia.protocols.group.Group;
import org.continuent.appia.protocols.group.ViewID;
import org.continuent.appia.protocols.group.events.GroupSendableEvent;

/**
 * Sequencer message containing the sequence number of some data message.
 * 
 * @author Nuno Carvalho
 */
public class SeqOrderEvent extends GroupSendableEvent {

	public SeqOrderEvent(Channel channel, int dir, Session source, Group group,
			ViewID view_id) throws AppiaEventException {
		super(channel, dir, source, group, view_id);
	}

	public SeqOrderEvent() {
		super();
	}

	public SeqOrderEvent(Channel channel, int dir, Session source, Group group,
			ViewID view_id, Message omsg) throws AppiaEventException {
		super(channel, dir, source, group, view_id, omsg);
	}

	public SeqOrderEvent(Message omsg) {
		super((Message) omsg);
	}
}
