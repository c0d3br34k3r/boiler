package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;

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
