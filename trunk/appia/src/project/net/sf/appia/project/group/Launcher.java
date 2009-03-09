package net.sf.appia.project.group;

import java.io.File;
import java.io.IOException;

import net.sf.appia.core.Appia;
import net.sf.appia.xml.AppiaXML;

import org.xml.sax.SAXException;

/**
 * The Launcher class only serves to reads xml config
 * files and start the various appias instances.
 * 
 * @author jtrindade
 */
public class Launcher {


	public static void main(String[] args) {
		final String filename = args[0];

		final File file = new File(filename);
		try {
			AppiaXML.load(file);
		} catch (SAXException e) {
			final Exception we = e.getException();
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
