package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Location;
import com.catascopic.template.TemplateParseException;
import com.google.common.collect.ImmutableList;

abstract class NodeBuilder implements Tag, BlockBuilder {

	private final Location location;

	NodeBuilder(Location location) {
		this.location = location;
	}

	private List<Node> nodes = new ArrayList<>();

	@Override
	public final void add(Node node) {
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
		throw new TemplateParseException(location, "else not allowed");
	}

	public Location location() {
		return location;
	}

}
