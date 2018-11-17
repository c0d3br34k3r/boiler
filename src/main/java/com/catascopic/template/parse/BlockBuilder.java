package com.catascopic.template.parse;

import com.catascopic.template.Locatable;

interface BlockBuilder extends Locatable {

	void add(Node node);

	Node buildElse(Node elseNode);

	Node build();

}
