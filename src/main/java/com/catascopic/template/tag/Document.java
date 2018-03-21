package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;

public class Document implements Node {

	public static Document parse(Reader reader) throws IOException, CarrotException {
		Parser parser = new Parser(reader);
		return new Document(parser);
	}

	private Document(Parser parser) throws IOException, CarrotException {
		for (;;) {
			Node node = parser.next();
			if (node == MarkerNode.END_DOCUMENT) {
				break;
			}
			
		}
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope) throws CarrotException, IOException {

	}

}
