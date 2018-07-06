package com.catascopic.template.parse;

import java.io.IOException;
import java.util.List;

import com.catascopic.template.Scope;

class Block {

	private final List<Node> nodes;
	private final Block linked;

	public Block(List<Node> nodes) {
		this(nodes, null);
	}

	public Block(List<Node> nodes, Block linked) {
		this.nodes = nodes;
		this.linked = linked;
	}

	void render(Appendable writer, Scope scope) throws IOException {
		for (Node node : nodes) {
			node.render(writer, scope);
		}
	}

	void renderLinked(Appendable writer, Scope scope) throws IOException {
		linked.render(writer, scope);
	}

}
