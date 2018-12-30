package com.catascopic.template;

import com.catascopic.template.eval.Term;

public abstract class SimpleContext implements Context {

	@Override
	public final Object apply(Term term) {
		return term.evaluate(this);
	}

}
