package com.catascopic.template.parse;

interface ParseResult {

	Type type();

	Node node();

	enum Type {
		NODE,
		ELSE_IF_NODE,
		ELSE_NODE,
		END_NODE,
		END_DOCUMENT;
	}
	
}
