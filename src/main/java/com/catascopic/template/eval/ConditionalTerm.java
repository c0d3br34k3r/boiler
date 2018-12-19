package com.catascopic.template.eval;

import com.catascopic.template.Context;
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
	public Object evaluate(Context context) {
		return Values.isTrue(condition.evaluate(context))
				? first.evaluate(context)
				: second.evaluate(context);
	}

	@Override
	public String toString() {
		return String.format("%s ? %s : %s)", condition, first, second);
	}

}
