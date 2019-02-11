package com.catascopic.template.expr;

interface Token {

	TokenType type();

	Object value();

	String identifier();

	Symbol symbol();

}
