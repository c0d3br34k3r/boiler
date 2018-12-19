package com.catascopic.template.eval;

import com.catascopic.template.Context;
import com.catascopic.template.Null;

enum NullTerm implements Term {

	NULL;

	@Override
	public Object evaluate(Context context) {
		return Null.NULL;
	}

	@Override
	public String toString() {
		return "null";
	}

}
