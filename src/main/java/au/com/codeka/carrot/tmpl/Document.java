package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;

public class Document extends Node {

	public static Document parse(Reader reader, Configuration config) {
		Document root = new Document();
		Parser parser = new Parser(reader);
		parse(parser, root, config);
		return root;
	}

	/**
	 * Parse tokens into the given {@link Node}.
	 * 
	 * @throws IOException
	 * @throws CarrotException
	 */
	private static void parse(Parser parser, Node node, Configuration config)
			throws IOException, CarrotException {
		Node current = node;
		for (;;) {
			Node child = parser.getNext();
			if (child == null) {
				break;
			}
			if (child.isBlock()) {
				parse(parser, child, config);
			}
			current.addChildNode(child);
		}
	}

	@Override
	void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		renderChildNodes(engine, writer, scope);
	}

}
