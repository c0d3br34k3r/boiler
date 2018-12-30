package com.catascopic.template.eval;

import com.catascopic.template.Context;

enum NullTerm implements Term {

	NULL;

	@Override
	public Object evaluate(Context context) {
		return null;
	}

	@Override
	public String toString() {
		return "null";
	}

}
