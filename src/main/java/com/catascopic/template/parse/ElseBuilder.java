package com.catascopic.template.parse;

import com.google.common.collect.ImmutableList;

abstract class ElseBuilder implements BlockBuilder, Tag {

	private ImmutableList.Builder<Node> nodes = ImmutableList.builder();
	private BlockBuilder elseBuilder;

	@Override
	public void add(Node node) {
		if (elseBuilder == null) {
			nodes.add(node);
		} else {
			elseBuilder.add(node);
		}
	}

	@Override
	public void setElse(BlockBuilder linked) {
		if (elseBuilder != null) {
			elseBuilder.setElse(linked);
		} else {
			elseBuilder = linked;
		}
	}

	abstract Node build(Block block, Node elseNode);

	@Override
	public final Node build() {
		return build(new Block(nodes.build()), elseBuilder == null
				? EmptyNode.EMPTY
				: elseBuilder.build());
	}

	@Override
	public String toString() {
		return elseBuilder == null ? super.toString()
				: super.toString() + ", elseBuilder=" + elseBuilder;
	}

}
