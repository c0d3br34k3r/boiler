package com.catascopic.template.expr;

import com.catascopic.template.Context;
import com.catascopic.template.value.Values;

class ValueTerm implements Term {

	private final Object value;

	ValueTerm(Object value) {
		this.value = value;
	}

	@Override
	public Object evaluate(Context context) {
		return value;
	}

	@Override
	public String toString() {
		return Values.uneval(value);
	}

}
