package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Location;
import com.google.common.collect.ImmutableList;

abstract class NodeBuilder extends BlockBuilder implements Tag {

	private List<Node> nodes = new ArrayList<>();

	NodeBuilder(Location location) {
		super(location);
	}

	@Override
	protected final void add(Node node) {
		nodes.add(node);
	}

	@Override
	public final Node build() {
		return build(new Block(ImmutableList.copyOf(nodes)));
	}

	protected abstract Node build(Block block);

	@Override
	public final Node buildElse(Node elseNode) {
		return build(new Block(ImmutableList.copyOf(nodes)), elseNode);
	}

	protected Node build(Block block, Node elseNode) {
		throw new UnsupportedOperationException();
	}

}
