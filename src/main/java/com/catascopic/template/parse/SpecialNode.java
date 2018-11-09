package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

public enum SpecialNode implements NodeCreator, Node {

	END,
	BREAK;

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(scope.newLine());
	}

	@Override
	public String toString() {
		return System.lineSeparator();
	}

}
