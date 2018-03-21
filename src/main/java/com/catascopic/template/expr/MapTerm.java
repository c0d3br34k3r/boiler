package com.catascopic.template.expr;

import java.util.Map;

import com.catascopic.template.Scope;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

class MapTerm implements Term {

	private final Map<String, Term> items;

	MapTerm(Map<String, Term> items) {
		this.items = items;
	}

	@Override
	public Object evaluate(Scope scope) {
		return Maps.transformValues(items, scope);
	}

	@Override
	public String toString() {
		return "{" + Joiner.on(", ").withKeyValueSeparator(": ").join(items) + "}";
	}

}
