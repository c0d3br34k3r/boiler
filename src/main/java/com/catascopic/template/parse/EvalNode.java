package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;

class EvalNode implements Node, Tag {

	private final Term expression;

	private EvalNode(Term expression) {
		this.expression = expression;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(Values.toString(expression.evaluate(scope)));
	}

	static Tag getTag(Tokenizer tokenizer) {
		return new EvalNode(tokenizer.parseExpression());
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

	@Override
	public String toString() {
		return "EVAL(" + expression + ")";
	}

}
