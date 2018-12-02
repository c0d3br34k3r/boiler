package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

enum NewlineNode implements Node, Tag {

	NEWLINE;

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(scope.newLine());
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

	@Override
	public String toString() {
		return "\n";
	}

}
