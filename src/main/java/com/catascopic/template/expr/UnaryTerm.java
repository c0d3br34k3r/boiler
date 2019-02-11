package com.catascopic.template.expr;

import com.catascopic.template.Context;

class UnaryTerm implements Term {

	private final UnaryOperator operator;
	private final Term term;

	public UnaryTerm(UnaryOperator operation, Term term) {
		this.operator = operation;
		this.term = term;
	}

	@Override
	public Object evaluate(Context context) {
		return operator.apply(term.evaluate(context));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", operator, term);
	}

}
