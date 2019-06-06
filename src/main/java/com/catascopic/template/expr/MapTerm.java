package com.catascopic.template.expr;

import java.util.Map;
import java.util.Map.Entry;

import com.catascopic.template.Context;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

class MapTerm implements Term {

	private final Map<String, Term> items;

	MapTerm(Map<String, Term> items) {
		this.items = items;
	}

	@Override
	public Object evaluate(Context context) {
		Builder<String, Object> builder = ImmutableMap.builder();
		for (Entry<String, Term> entry : items.entrySet()) {
			builder.put(entry.getKey(), entry.getValue().evaluate(context));
		}
		return builder.build();
	}

	@Override
	public String toString() {
		return items.toString();
	}

}
