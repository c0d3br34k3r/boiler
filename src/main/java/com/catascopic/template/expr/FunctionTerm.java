package com.catascopic.template.expr;

import java.util.List;

import com.catascopic.template.Context;
import com.google.common.base.Joiner;

class FunctionTerm implements Term {

	private final String name;
	private final List<Term> params;

	FunctionTerm(String name, List<Term> params) {
		this.name = name;
		this.params = params;
	}

	@Override
	public Object evaluate(Context context) {
		return context.call(name, ListTerm.evaluateList(params, context));
	}

	@Override
	public String toString() {
		return name + "(" + Joiner.on(", ").join(params) + ")";
	}

}
