package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

public enum NewlineNode implements Tag, Node {

	NEWLINE;

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(scope.newLine());
	}

	@Override
	public void build(BlockBuilder builder) {
		builder.add(NEWLINE);
	}

}
