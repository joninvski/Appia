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
/*
 * Created on Mar 23, 2004
 */
package org.continuent.appia.test.xml;

/**
 * @author Liliana Rosa & Nuno Almeida 
 *
 */
public class ConnectorInterface {
	
	private ConnectorGraphics con;
	
	/*
	 * Normal Constructor
	 */
	public ConnectorInterface(String user, String gossip_host, int gossip_port){
		con = new ConnectorGraphics(user,gossip_host,gossip_port);
	}
	/*
	 * Constructor for usage with IntegrityLayer (needs a secrets) 
	 */
	public ConnectorInterface(String user, String gossip_host, int gossip_port,String secret){
		con = new ConnectorGraphics(user,gossip_host,gossip_port,secret);
	}
}
