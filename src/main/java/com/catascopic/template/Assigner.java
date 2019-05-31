package com.catascopic.template;

/**
 * Functional interface that assigns one or more variables to a given scope.
 */
public interface Assigner {

	/**
	 * Assigns one or more variables to a given scope.
	 * 
	 * @param scope the given scope
	 */
	void assign(Scope scope);
}
