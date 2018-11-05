package com.catascopic.template.eval;

import com.catascopic.template.Scope;
import com.google.common.base.Preconditions;

class ValueTerm implements Term {

	private final Object value;

	ValueTerm(Object value) {
		this.value = Preconditions.checkNotNull(value);
	}

	@Override
	public Object evaluate(Scope scope) {
		return value;
	}

	@Override
	public String toString() {
		return value instanceof String ? "'" + value + "'" : value.toString();
	}

}
