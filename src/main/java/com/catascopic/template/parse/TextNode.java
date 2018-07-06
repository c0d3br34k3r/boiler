package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;

class TextNode implements Node {

	private final Term fileName;

	TextNode(Term text) {
		this.fileName = text;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		scope.renderTextFile(writer, Values.toString(fileName.evaluate(scope)));
	}

}
