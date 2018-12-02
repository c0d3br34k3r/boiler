package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

class TextNode implements Node, Tag {

	private final String text;

	private TextNode(String content) {
		this.text = content;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(text);
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

	static Tag getTag(final String text) {
		return new TextNode(text);
	}

	@Override
	public String toString() {
		return text;
	}

}
