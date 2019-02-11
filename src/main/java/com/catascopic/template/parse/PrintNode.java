package com.catascopic.template.parse;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;

class PrintNode implements Node, Tag {

	private final Term expression;

	private PrintNode(Term expression) {
		this.expression = expression;
	}

	@Override
	public void render(Appendable writer, Scope scope) {
		// TODO: location
		scope.print(null, String.valueOf(expression.evaluate(scope)));
	}

	static Tag getTag(Tokenizer tokenizer) {
		return new PrintNode(tokenizer.parseTopLevelExpression());
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

	@Override
	public String toString() {
		return "print [" + expression + "]";
	}

}
