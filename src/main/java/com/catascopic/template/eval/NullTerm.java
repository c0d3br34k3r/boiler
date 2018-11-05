package com.catascopic.template.eval;

import com.catascopic.template.Null;
import com.catascopic.template.Scope;

enum NullTerm implements Term {

	NULL;

	@Override
	public Object evaluate(Scope scope) {
		return Null.NULL;
	}

	@Override
	public String toString() {
		return "null";
	}

}
