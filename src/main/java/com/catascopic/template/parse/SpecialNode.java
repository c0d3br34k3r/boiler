package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

public enum SpecialNode implements Tag, Node {

	NEWLINE,
	END,
	END_DOCUMENT;

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(scope.newLine());
	}

	@Override
	public String toString() {
		return System.lineSeparator();
	}

	@Override
	public Node createNode(TagStream stream) {
		return this;
	}

}
