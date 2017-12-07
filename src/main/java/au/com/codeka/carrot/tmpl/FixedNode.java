package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.tmpl.parse.Segment;

/**
 * A {@link FixedNode} represents the text outside of the {% ... %} tags: the
 * text that's just "fixed".
 */
public class FixedNode extends Node {

	private String content;

	private FixedNode(String content) {
		super(false);
		this.content = content;
	}

	public static FixedNode create(Segment token) {
		return new FixedNode(token.getContent());
	}

	public String getContent() {
		return content;
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope) throws IOException {
		writer.write(content);
	}

}
