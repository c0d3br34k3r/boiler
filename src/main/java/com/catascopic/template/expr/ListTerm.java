package com.catascopic.template.expr;

import java.util.List;

import com.catascopic.template.Context;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

class ListTerm implements Term {

	private final List<Term> items;

	ListTerm(List<Term> items) {
		this.items = items;
	}

	@Override
	public Object evaluate(Context context) {
		return evaluateList(items, context);
	}

	static List<Object> evaluateList(List<Term> terms, Context context) {
		Builder<Object> builder = ImmutableList.builder();
		for (Term term : terms) {
			builder.add(term.evaluate(context));
		}
		return builder.build();
	}

	@Override
	public String toString() {
		return items.toString();
	}

}
