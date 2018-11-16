package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

public class TextNode implements Node, Tag {

	private final String text;

	public TextNode(String content) {
		this.text = content;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(text);
	}

	@Override
	public String toString() {
		return '"' + text + '"';
	}

	@Override
	public Node createNode(TagStream stream) {
		return this;
	}

}
