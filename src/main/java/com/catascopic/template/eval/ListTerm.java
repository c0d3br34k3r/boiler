package com.catascopic.template.eval;

import java.util.List;

import com.catascopic.template.Scope;
import com.google.common.collect.Lists;

class ListTerm implements Term {

	private final List<Term> items;

	ListTerm(List<Term> items) {
		this.items = items;
	}

	@Override
	public Object evaluate(Scope scope) {
		return Lists.transform(items, scope);
	}

	@Override
	public String toString() {
		return items.toString();
	}

}
