package com.catascopic.template.eval;

public interface Token {

	TokenType type();

	Object value();

	String identifier();

	Symbol symbol();

}
