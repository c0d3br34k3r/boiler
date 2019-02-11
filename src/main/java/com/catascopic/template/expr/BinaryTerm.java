package com.catascopic.template.expr;

import com.catascopic.template.Context;

class BinaryTerm implements Term {

	private final Term left;
	private final BinaryOperator operator;
	private final Term right;

	BinaryTerm(Term left, BinaryOperator operation, Term right) {
		this.left = left;
		this.operator = operation;
		this.right = right;
	}

	@Override
	public Object evaluate(Context context) {
		return operator.apply(left, right, context);
	}

	@Override
	public String toString() {
		return String.format("%s(%s, %s)", operator, left, right);
	}

}
