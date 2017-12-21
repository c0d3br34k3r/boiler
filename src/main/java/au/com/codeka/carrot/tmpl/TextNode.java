package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.Scope;

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
