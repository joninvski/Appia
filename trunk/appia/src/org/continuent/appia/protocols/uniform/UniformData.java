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

import java.util.*;

/**
 * Stores information about the events that must be buffered 
 * before being delivered.
 */
public class UniformData {

    private HashMap hash;

    public UniformData(){
        hash = new HashMap();
    }
    
    /**
     * gets the number of messages in this container.
     * @return the number of messages in this container.
     */
    public int size(){
    	return hash.size();
    }

    /**
     * Removes all messages from this container.
     *
     */
    public void clean(){
    	hash.clear();
    }
    
    public void put(UniformNode node){
        hash.put(node.getId(),node);
    }

    public UniformNode get(UniformHeader header){
        return (UniformNode) hash.get(header);
    }

    public boolean exists(UniformHeader header){
        return hash.containsKey(header);
    }

    /**
     * Updates Acknoledgement info about one specific message.
     * Return the message of a majority of nodes had already seen it. If the message is returned,
     * it is also removed from this container.
     * @param header the header that represents the message.
     * @param majority the number of nodes that define a majority.
     * @return The message if a majority of nodes had seen it, null otherwise.
     */
    public UniformNode update(UniformHeader header, int majority){
        if(((UniformNode)hash.get(header)).addSeen(majority)){
        	UniformNode node = (UniformNode) hash.remove(header);
            return node;
        }
        else
            return null;
    }
}
