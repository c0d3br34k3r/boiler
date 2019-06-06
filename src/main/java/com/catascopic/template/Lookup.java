package com.catascopic.template;

public interface Lookup {

	/**
	 * Gets the value of a referenced variable.
	 * 
	 * @param name the name of the variable
	 * 
	 * @throws TemplateRenderException if the referenced variable does not exist
	 */
	Object get(String name);

}
