package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

class ContentNode implements Node {

	private final String content;

	ContentNode(String content) {
		this.content = content;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(content);
	}

}
