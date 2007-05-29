
/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2007 University of Lisbon
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
 * Initial developer(s): Jose Mocito.
 * Contributor(s): See Appia web page for a list of contributors.
 */
package org.continuent.appia.protocols.uniform;

import org.continuent.appia.core.message.Message;

public class MessageContainer {

	private int orig;
	private long sn;
	private Message message;
	
	public MessageContainer(int orig, long sn, Message message) {
		this.orig = orig;
		this. sn = sn;
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public int getOrig() {
		return orig;
	}

	public void setOrig(int orig) {
		this.orig = orig;
	}

	public long getSn() {
		return sn;
	}

	public void setSn(long sn) {
		this.sn = sn;
	}

	public boolean equals(MessageContainer cont) {
		if (orig == cont.getOrig() && sn == cont.getSn())
			return true;
		return false;
	}
}
