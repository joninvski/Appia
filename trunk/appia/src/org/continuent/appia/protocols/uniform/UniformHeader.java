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
 package org.continuent.appia.protocols.uniform;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Header used to identify the messages
 */
class UniformHeader implements Externalizable {

	private static final long serialVersionUID = -7628025568002091260L;

	private long seqNumber;

	private long sender;

	public UniformHeader() {}
	
	/**
	 * Basic constructor.
	 * @param seq Sequence number
	 * @param send The process id that sended the message.
	 */
	public UniformHeader(long seq, long send) {
		seqNumber = seq;
		sender = send;
	}

	/**
	 * Sets the sequence number.
	 * @param seq Sequence number
	 */
	public void setSeqNumber(long seq) {
		seqNumber = seq;
	}

	/**
	 * Sets the sender.
	 * @param send The process id that sended the message.
	 */
	public void setSender(long send) {
		sender = send;
	}

	/**
	 * Gets the sequence number.
	 * @return the sequence number
	 */
	public long getSeqNumber() {
		return seqNumber;
	}

	/**
	 * Gets the rank of the sender of the message
	 */
	public long getSender() {
		return sender;

	}

	public int hashCode() {
		return (int) (seqNumber ^ sender);
	}
	
	public boolean equals(Object obj){
		if(obj instanceof UniformHeader){
			UniformHeader h = (UniformHeader) obj;
			return h.sender == sender && h.seqNumber == seqNumber;
		}
		else return false;
	}

	public String toString(){
		return "UniformHeader - SEQ="+seqNumber+" SENDER="+sender;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(seqNumber);
		out.writeLong(sender);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		seqNumber = in.readLong();
		sender = in.readLong();
	}
}