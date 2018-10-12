package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.expr.Term;

class EvalNode implements Node {

	private final Term term;

	EvalNode(Term term) {
		this.term = term;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(Values.toString(term.evaluate(scope)));
	}

	@Override
	public String toString() {
		return term.toString();
	}

}
