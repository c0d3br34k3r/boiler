package com.catascopic.template.expr;

import com.catascopic.template.Context;
import com.catascopic.template.value.Values;

class IndexTerm implements Term {

	private final Term term;
	private final Term index;

	public IndexTerm(Term term, Term index) {
		this.term = term;
		this.index = index;
	}

	@Override
	public Object evaluate(Context context) {
		return Values.index(term.evaluate(context), index.evaluate(context));
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", term, index);
	}

}
