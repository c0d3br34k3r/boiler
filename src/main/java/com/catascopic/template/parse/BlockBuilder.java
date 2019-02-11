package com.catascopic.template.parse;

import com.catascopic.template.Location;

interface BlockBuilder {

	void add(Node node);

	Node buildElse(Node elseNode);

	Node build();
	
	Location location();

}
