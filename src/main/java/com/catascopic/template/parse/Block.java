package com.catascopic.template.parse;

import java.io.IOException;
import java.util.List;

import com.catascopic.template.Scope;
import com.google.common.base.Joiner;

class Block {

	private final List<Node> nodes;
	private final Node elseNode;

	Block(List<Node> nodes) {
		this(nodes, EMPTY);
	}

	Block(List<Node> nodes, Node elseNode) {
		this.nodes = nodes;
		this.elseNode = elseNode;
	}

	void render(Appendable writer, Scope scope) throws IOException {
		for (Node node : nodes) {
			node.render(writer, scope);
		}
	}

	void renderElse(Appendable writer, Scope scope) throws IOException {
		elseNode.render(writer, scope);
	}

	@Override
	public String toString() {
		return "{ " + (elseNode == EMPTY
				? Joiner.on(" ").join(nodes)
				: Joiner.on(" ").join(nodes)
				+ " } else { " + elseNode) + " }";
	}

	private static final Node EMPTY = new Node() {

		@Override
		public void render(Appendable writer, Scope scope) throws IOException {
			// do nothing
		}
	};

}
