package com.catascopic.template.expr;

import com.catascopic.template.Context;
import com.catascopic.template.value.Values;
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
		return Values.uneval(value);
	}

}
