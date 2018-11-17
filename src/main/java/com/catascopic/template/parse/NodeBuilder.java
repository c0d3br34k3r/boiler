package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

abstract class NodeBuilder implements BlockBuilder, Tag {

	private List<Node> nodes = new ArrayList<>();

	@Override
	public void add(Node node) {
		nodes.add(node);
	}

	@Override
	public void setElse(BlockBuilder linked) {
		throw new IllegalArgumentException();
	}

	@Override
	public Node build() {
		return build(new Block(ImmutableList.copyOf(nodes)));
	}

	abstract Node build(Block block);

	@Override
	public String toString() {
		return nodes.toString();
	}

}
