package com.catascopic.template.eval;

import java.util.Map;

import com.catascopic.template.Context;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

class MapTerm implements Term {

	private final Map<String, Term> items;

	MapTerm(Map<String, Term> items) {
		this.items = items;
	}

	@Override
	public Object evaluate(Context context) {
		return Maps.transformValues(items, context);
	}

	@Override
	public String toString() {
		return "{" + Joiner.on(", ").withKeyValueSeparator(": ").join(items)
				+ "}";
	}

}
