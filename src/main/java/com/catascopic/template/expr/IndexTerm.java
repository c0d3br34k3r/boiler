package com.catascopic.template.expr;

import com.catascopic.template.Scope;

class IndexTerm implements Term {

	private final Term term;
	private final Term index;

	public IndexTerm(Term term, Term index) {
		this.term = term;
		this.index = index;
	}

	@Override
	public Object evaluate(Scope scope) {
		return Values.index(term.evaluate(scope), index.evaluate(scope));
	}

	@Override
	public String toString() {
		return String.format("(INDEX %s, %s)", term.toString(), index.toString());
	}

}
