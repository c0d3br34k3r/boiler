package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;

public abstract class DynamicNode extends Node {

	private List<Node> childNodes = new ArrayList<>();
	private Node linkedNode; // = null

	void addChildNode(Node node) {
		childNodes.add(node);
	}

	@Override
	void link(Node node) {
		this.linkedNode = node;
	}

	@Override
	Node getLinkedNode() {
		return linkedNode;
	}

	@Override
	boolean isBlock() {
		return true;
	}

	@Override
	Iterable<Node> getChildNodes() {
		return childNodes;
	}

	@Override
	abstract void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException;

}
