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
 
package net.sf.appia.protocols.tcpcomplete;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SenderQueue<T> {

	private BlockingQueue<T> mailbox = new LinkedBlockingQueue<T>();

	public SenderQueue() {}

	public void add(T item){
	    try {
            mailbox.put(item);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	public T removeNext(){
	    try {
            return mailbox.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
	}

	public T removeNext(long millis){
        try {
            return mailbox.poll(millis,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public int getSize(){
		return mailbox.size();
	}

}
