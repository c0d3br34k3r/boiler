package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

abstract class NodeBuilder {

	private List<Node> nodes = new ArrayList<>();

	void add(Node node) {
		nodes.add(node);
	}

	void setElse(NodeBuilder builder) {
		throw new IllegalStateException();
	}

	protected Block getBlock() {
		return new Block(ImmutableList.copyOf(nodes));
	}

	abstract Node build();

	@Override
	public String toString() {
		return nodes.toString();
	}

}
