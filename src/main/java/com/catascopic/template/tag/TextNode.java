package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.Scope;

class TextNode implements Node {

	private final String content;

	TextNode(String content) {
		this.content = content;
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope) throws IOException {
		writer.write(content);
	}

}
