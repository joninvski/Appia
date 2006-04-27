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
 * 
 */
package org.continuent.appia.protocols.group.stable;

import org.continuent.appia.core.message.Message;

public class StableInfo {
  public long seqno;
  public String eventName;
  public Message omsg;
  public StableInfo next;
  
  public StableInfo(long seqno, String eventName, Message omsg) {
    this.seqno=seqno;
    this.omsg=omsg;
    this.eventName=eventName;
    this.next=null;
  }
}