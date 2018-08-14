package com.catascopic.template.expr;

import com.catascopic.template.Scope;

public enum NullTerm implements Term {

	NULL;

	@Override
	public Object evaluate(Scope scope) {
		return null;
	}

}
