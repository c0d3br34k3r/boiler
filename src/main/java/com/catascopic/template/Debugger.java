package com.catascopic.template;

import java.io.IOException;

/**
 * Handles the messages produced by print tags in a template.
 */
public interface Debugger {

	/**
	 * Writes a given message to a user-friendly location, usually either by
	 * printing it to the standard output or appending it to a file.
	 * 
	 * @param location the location of the print tag in the template
	 * @param message the message
	 * @throws IOException if an I/O error occurs while writing the message
	 */
	void print(Location location, String message) throws IOException;

}
