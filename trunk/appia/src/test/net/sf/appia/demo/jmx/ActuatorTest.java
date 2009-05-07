/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2009 INESC-ID/IST
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

package net.sf.appia.demo.jmx;

import javax.management.Attribute;

import net.sf.appia.management.AppiaManagementException;
import net.sf.appia.management.jmx.GenericActuator;

public class ActuatorTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        if(args.length != 3){
            System.out.println("Usage: java "+ActuatorTest.class.getName()+" <host> <port> <channelName>");
            System.exit(-1);
        }
        
        GenericActuator actuator = new GenericActuator();
        try {
            actuator.connect(args[0], Integer.parseInt(args[1]), args[2]);
            System.out.println("Current group attribute: "+actuator.getAttribute("remoteaddr:group"));
            actuator.setAttribute(new Attribute("remoteaddr:group","NewGroupID"));
            System.out.println("Current group attribute: "+actuator.getAttribute("remoteaddr:group"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (AppiaManagementException e) {
            e.printStackTrace();
        }

    }

}
