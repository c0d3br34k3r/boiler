package com.catascopic.template.parse;

interface BlockBuilder {

	void add(Node node);

	Node buildElse(Node elseNode);

	Node build();

}
