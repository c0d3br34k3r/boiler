package com.catascopic.template.parse;

interface BlockBuilder {

	void add(Node node);

	void setElse(BlockBuilder builder);

	Node build();

}
