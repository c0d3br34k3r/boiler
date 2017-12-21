package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;

abstract class BlockNode implements Node {

	private List<Node> children = new ArrayList<>();
	private Node linked; // = null

	protected void renderLinked(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		linked.render(engine, writer, scope);
	}

	protected void renderChildren(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		for (Node child : children) {
			child.render(engine, writer, scope);
		}
	}

}
