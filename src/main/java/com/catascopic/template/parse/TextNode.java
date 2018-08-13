package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.expr.Term;

class TextNode implements Node {

	private final Term fileName;

	TextNode(Term text) {
		this.fileName = text;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		scope.renderTextFile(writer, Values.toString(fileName.evaluate(scope)));
	}

	@Override
	public String toString() {
		return "text: " + fileName;
	}

}
