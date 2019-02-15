package com.catascopic.template.parse;

import com.catascopic.template.Location;
import com.catascopic.template.TemplateParseException;

abstract class BlockBuilder {

	private final Location location;

	BlockBuilder(Location location) {
		this.location = location;
	}

	BlockBuilder(BlockBuilder copy) {
		this.location = copy.location;
	}

	protected abstract void add(Node node);

	protected Node buildElse(Node elseNode) {
		throw new UnsupportedOperationException();
	}

	protected abstract Node build();

	protected void checkElse(BlockBuilder elseBuilder) {
		throw new TemplateParseException(elseBuilder.location, "else not allowed in " + this);
	}

}
