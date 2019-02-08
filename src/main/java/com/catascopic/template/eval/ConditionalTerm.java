package com.catascopic.template.eval;

import com.catascopic.template.Context;
import com.catascopic.template.Values;

class ConditionalTerm implements Term {

	private final Term condition;
	private final Term ifTrue;
	private final Term ifFalse;

	public ConditionalTerm(Term condition, Term ifTrue, Term ifFalse) {
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	@Override
	public Object evaluate(Context context) {
		return Values.isTrue(condition.evaluate(context))
				? ifTrue.evaluate(context)
				: ifFalse.evaluate(context);
	}

	@Override
	public String toString() {
		return String.format("%s ? %s : %s)", condition, ifTrue, ifFalse);
	}

}
