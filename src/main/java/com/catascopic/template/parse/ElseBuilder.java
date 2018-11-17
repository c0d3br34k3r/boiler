package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

abstract class ElseBuilder implements BlockBuilder, Tag {

	private List<Node> nodes = new ArrayList<>();
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
		return build(new Block(ImmutableList.copyOf(nodes)), elseBuilder == null
				? EmptyNode.EMPTY
				: elseBuilder.build());
	}

	@Override
	public String toString() {
		return elseBuilder == null ? super.toString()
				: super.toString() + ", elseBuilder=" + elseBuilder;
	}

}
