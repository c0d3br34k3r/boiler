package com.catascopic.template.parse;

import java.io.IOException;
import java.util.List;

import com.catascopic.template.Scope;

class Block {

	private final List<Node> nodes;
	private final Node elseNode;

	Block(List<Node> nodes) {
		this(nodes, EmptyNode.INSTANCE);
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
		return elseNode == EmptyNode.INSTANCE
				? nodes.toString()
				: nodes.toString() + " else " + elseNode;
	}

}
