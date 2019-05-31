package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Location;
import com.catascopic.template.Scope;
import com.catascopic.template.TemplateRenderException;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;

class PrintNode implements Node, Tag {

	private final Term expression;
	private final Location location;

	private PrintNode(Location location, Term expression) {
		this.location = location;
		this.expression = expression;
	}

	@Override
	public void render(Appendable writer, Scope scope) {
		try {
			scope.print(location, String.valueOf(expression.evaluate(scope)));
		} catch (IOException e) {
			throw new TemplateRenderException(e).addLocation(location);
		}
	}

	static Tag getTag(Tokenizer tokenizer) {
		return new PrintNode(tokenizer.getLocation(), tokenizer.parseTopLevelExpression());
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
