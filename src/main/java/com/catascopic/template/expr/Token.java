package com.catascopic.template.expr;

public interface Token {

	TokenType type();

	Object value();

	String identifier();

	Symbol symbol();

}
