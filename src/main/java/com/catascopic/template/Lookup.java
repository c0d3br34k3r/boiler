package com.catascopic.template;

/**
 * General-purpose interface that allows access to the values of variables by
 * their name.
 */
public interface Lookup {

	/**
	 * Gets the value of a named variable.
	 * 
	 * @param name the name of the variable
	 * @return the value of the variable
	 * 
	 * @throws TemplateRenderException if the referenced variable does not exist
	 */
	Object get(String name);

}
