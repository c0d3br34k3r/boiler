package au.com.codeka.carrot.expr;

import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import au.com.codeka.carrot.Scope;

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
