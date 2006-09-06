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
package org.continuent.appia.demo.xml;

import java.io.File;
import java.io.IOException;

import org.continuent.appia.core.*;
import org.continuent.appia.test.xml.ConnectorInterface;
import org.continuent.appia.xml.AppiaXML;
import org.xml.sax.SAXException;

/**
 * @author Liliana Rosa & Nuno Almeida
 *
 */
public class Messenger {
	
	public static void main(String args[]) {
		
		/*
		 * Number of arguments is 4 if no secret is given
		 * otherwise it's 5 args
		 */
		if (args.length != 4 && args.length != 5) {
			System.out.println("Invalid number of arguments!");
			System.out.println("Usage: java demo.xml.Messenger <username> <gossip_host> <gossip_port> <filexml> [<secret>]");
			System.exit(0);
		}
		
		String secret = null;
		String username = args[0];
		String gossip_host = args[1];
		int gossip_port = Integer.parseInt(args[2]);
		String filename = args[3];

		File file = new File(filename);
		try {
			AppiaXML.load(file);
		} catch (SAXException e) {
			Exception we = e.getException();
			if (we != null )
				we.printStackTrace();
			else
				e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(args.length == 5){
			secret = args[4];
			new ConnectorInterface(username,gossip_host,gossip_port,secret);
		} else
			new ConnectorInterface(username,gossip_host,gossip_port);
		
		Appia.run();
    }	
}
