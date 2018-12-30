package com.catascopic.template.eval;

import java.util.List;

import com.catascopic.template.Context;
import com.google.common.collect.Lists;

class ListTerm implements Term {

	private final List<Term> items;

	ListTerm(List<Term> items) {
		this.items = items;
	}

	@Override
	public Object evaluate(Context context) {
		return Lists.transform(items, context);
	}

	@Override
	public String toString() {
		return items.toString();
	}

}
