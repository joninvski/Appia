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
package net.sf.appia.protocols.total.switching;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.AppiaException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.Timer;

/**
 * This class defines a timer that is associated with the sending of a 
 * NullEvent in the Total Order Switching protocol.
 * 
 * @author Jose Mocito
 * @version 0.7
 */
public class NullEventTimer extends Timer {

	public NullEventTimer() {
		super();
	}

	public NullEventTimer(long when, String timerID, Channel channel, int dir,
			Session source, int qualifier) throws AppiaEventException,
			AppiaException {
		super(when, timerID, channel, dir, source, qualifier);
	}

}
