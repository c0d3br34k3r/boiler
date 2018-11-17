package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Location;
import com.catascopic.template.Scope;

class TextNode implements Node {

	private final String text;

	private TextNode(String content) {
		this.text = content;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(text);
	}

	@Override
	public String toString() {
		return text;
	}

	static Tag getTag(Location location, final String text) {
		return new Tag(location) {
			
			@Override
			public void handle(TemplateParser parser) {
				parser.add(new TextNode(text));
			}
		};
	}

}
