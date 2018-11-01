package com.catascopic.template.expr;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;

class ConditionalTerm implements Term {

	private final Term condition;
	private final Term first;
	private final Term second;

	public ConditionalTerm(Term condition, Term first, Term second) {
		this.condition = condition;
		this.first = first;
		this.second = second;
	}

	@Override
	public Object evaluate(Scope scope) {
		return Values.isTrue(condition.evaluate(scope))
				? first.evaluate(scope)
				: second.evaluate(scope);
	}

	@Override
	public String toString() {
		return String.format("CONDITIONAL(%s, %s, %s)",
				condition, first, second);
	}

}
