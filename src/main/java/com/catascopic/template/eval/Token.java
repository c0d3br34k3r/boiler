package com.catascopic.template.eval;

interface Token {

	TokenType type();

	Object value();

	String identifier();

	Symbol symbol();

}
