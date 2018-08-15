package com.catascopic.template.expr;

import java.util.List;

import com.catascopic.template.Params;
import com.catascopic.template.Scope;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

class FunctionTerm implements Term {

	private String name;
	private List<Term> params;

	FunctionTerm(String name, List<Term> params) {
		this.name = name;
		this.params = params;
	}

	@Override
	public Object evaluate(Scope scope) {
		return scope.getFunction(name).apply(new Params(Lists.transform(params,
				scope)));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", name, Joiner.on(", ").join(params));
	}

}
