package com.catascopic.template.expr;

import com.catascopic.template.Scope;

enum NullTerm implements Term {

	NULL;

	@Override
	public Object evaluate(Scope scope) {
		return null;
	}

	@Override
	public String toString() {
		return "null";
	}

}
