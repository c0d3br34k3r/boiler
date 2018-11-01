package com.catascopic.template.expr;

import com.catascopic.template.Scope;

class UnaryTerm implements Term {

	private final UnaryOperator operator;
	private final Term term;

	public UnaryTerm(UnaryOperator operation, Term term) {
		this.operator = operation;
		this.term = term;
	}

	@Override
	public Object evaluate(Scope scope) {
		return operator.apply(term.evaluate(scope));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", operator, term);
	}

}
