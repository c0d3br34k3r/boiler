package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

public enum BreakNode implements Node {

	INSTANCE;

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(scope.newLine());
	}

	@Override
	public String toString() {
		return System.lineSeparator();
	}

}
