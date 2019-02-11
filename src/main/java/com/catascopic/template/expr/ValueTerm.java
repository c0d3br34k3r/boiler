package com.catascopic.template.expr;

import com.catascopic.template.Context;
import com.google.common.base.Preconditions;

class ValueTerm implements Term {

	private final Object value;

	ValueTerm(Object value) {
		this.value = Preconditions.checkNotNull(value);
	}

	@Override
	public Object evaluate(Context context) {
		return value;
	}

	@Override
	public String toString() {
		return value instanceof String ? "'" + value + "'" : value.toString();
	}

}
