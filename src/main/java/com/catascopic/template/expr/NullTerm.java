package com.catascopic.template.expr;

import com.catascopic.template.Scope;

enum NullTerm implements Term {

	INSTANCE;

	@Override
	public Object evaluate(Scope scope) {
		return null;
	}

}
