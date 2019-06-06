package com.catascopic.template;

import java.util.List;

/**
 * The context in which an expression is evaluated. An expression that
 * references variables or functions will resolve these references through a
 * Context object.
 */
public interface Context extends Lookup {

	/**
	 * Calls the referenced function with the given arguments.
	 * 
	 * @param name the name of the function
	 * @param arguments the arguments of the function
	 * 
	 * @throws TemplateRenderException if the referenced function does not exist
	 */
	Object call(String name, List<Object> arguments);

}
