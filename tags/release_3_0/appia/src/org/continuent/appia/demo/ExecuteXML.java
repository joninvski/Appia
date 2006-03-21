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
 * Created on Aug 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.continuent.appia.demo;

import java.io.File;
import java.io.IOException;

import org.continuent.appia.core.Appia;
import org.continuent.appia.xml.AppiaXML;
import org.xml.sax.SAXException;


/**
 * Load and starts Appia with a given configuration.
 * @author Susana Guedes
 *
 */
public class ExecuteXML {
	
	public static void main(String args[]){
		/*
		 * Number of arguments is 1 
		 */
		if (args.length != 1) {
			System.out.println("Invalid number of arguments!");
			System.out.println("Usage: java demo.ExecuteXML <configuration XML file>");
			System.exit(0);
		}
		
		String filename = args[0];
		File file = new File(filename);
		try {
			AppiaXML.loadAndRun(file);
		} catch (SAXException e) {
			Exception we = e.getException();
			if (we != null )
				we.printStackTrace();
			else
				e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Appia.run();
    }	

}
