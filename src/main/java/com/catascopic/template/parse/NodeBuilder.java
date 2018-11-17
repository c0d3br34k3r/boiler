package com.catascopic.template.parse;

import com.google.common.collect.ImmutableList;

abstract class NodeBuilder implements BlockBuilder, Tag {

	private ImmutableList.Builder<Node> nodes = ImmutableList.builder();

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
		return build(new Block(nodes.build()));
	}

	abstract Node build(Block block);

}
