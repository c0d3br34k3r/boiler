package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.google.common.base.CharMatcher;

class TextNode implements Node, Tag {

	private final String text;

	private TextNode(String content) {
		this.text = content;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(text);
	}

	static Tag getTag(String text) {
		return new TextNode(text);
	}

	@Override
	public void handle(TagCleaner cleaner) {
		if (!CharMatcher.whitespace().matchesAllOf(text)) {
			cleaner.whitespace();
		}
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

	@Override
	public String toString() {
		return "\"" + text + "\"";
	}

}
