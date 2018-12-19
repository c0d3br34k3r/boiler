package com.catascopic.template.eval;

import java.util.List;

import com.catascopic.template.Scope;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

class FunctionTerm implements Term {

	private final String name;
	private final List<Term> params;

	FunctionTerm(String name, List<Term> params) {
		this.name = name;
		this.params = params;
	}

	@Override
	public Object evaluate(Scope scope) {
		return scope.call(name, Lists.transform(params, scope));
	}

	@Override
	public String toString() {
		return name + "(" + Joiner.on(", ").join(params) + ")";
	}

}
