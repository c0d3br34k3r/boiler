package com.catascopic.template.parse;

import com.catascopic.template.Scope;

/**
 * Functional interface that assigns one or more variables to a given scope.
 */
interface Assigner {

	/**
	 * Assigns one or more variables to a given scope.
	 * 
	 * @param scope the given scope
	 */
	void assign(Scope scope);
}
