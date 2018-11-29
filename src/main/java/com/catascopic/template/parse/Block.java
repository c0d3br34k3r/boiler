package com.catascopic.template.parse;

import java.io.IOException;
import java.util.List;

import com.catascopic.template.Scope;

class Block implements Node {

	private final List<Node> nodes;

	Block(List<Node> nodes) {
		this.nodes = nodes;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		for (Node node : nodes) {
			node.render(writer, scope);
		}
	}

	@Override
	public String toString() {
		return nodes.toString();
	}

}
