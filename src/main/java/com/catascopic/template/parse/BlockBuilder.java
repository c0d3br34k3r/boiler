package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Location;
import com.catascopic.template.TemplateParseException;
import com.google.common.collect.ImmutableList;

abstract class BlockBuilder {

	private List<Node> nodes = new ArrayList<>();

	void add(Node node) {
		nodes.add(node);
	}

	void setElse(NodeBuilderTag builder) {
		throw new TemplateParseException((Location) null, 
				"else not allowed: %s", builder);
	}

	protected Block getBlock() {
		return new Block(ImmutableList.copyOf(nodes));
	}

	abstract Node build();

	@Override
	public String toString() {
		return "nodes=" + nodes.toString();
	}

}
