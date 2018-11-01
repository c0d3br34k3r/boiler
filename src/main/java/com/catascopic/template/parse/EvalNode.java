package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.expr.Term;

class EvalNode implements Node {

	private final Term expression;

	EvalNode(Term expression) {
		this.expression = expression;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(Values.toString(expression.evaluate(scope)));
	}

	@Override
	public String toString() {
		return "<< " + expression.toString() + " >>";
	}

}
