package au.com.codeka.carrot.expr;

public interface Token {

	TokenType type();

	Object value();

	String identifier();

	Symbol symbol();

}
