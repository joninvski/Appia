
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
 * Created on 06-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.continuent.appia.protocols.total.seto;

import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.AppiaException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Session;
import org.continuent.appia.core.events.channel.Timer;
import org.continuent.appia.protocols.total.seto.ListContainer;

/**
 * Timer used when the protocol needs to delay the delivery of messages.
 * 
 * @author Nuno Carvalho
 */
public class SETOTimer extends Timer {

	ListContainer container;
	
	public SETOTimer() {
		super();
	}

	public SETOTimer(long when, Channel channel, int dir,
			Session source, int qualifier, ListContainer c) throws AppiaEventException,
			AppiaException {
		super(when, "fastAB@", channel, dir, source, qualifier);
		this.timerID += this;
		container = c;
	}
}
