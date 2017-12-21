package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;

/**
 * Set tag allows you to set the value a variable in the current scope to the
 * string value of the contents of the set block.
 */
public class SetNode implements Node {

	private BindingCreator assignments;

	SetNode(Tokenizer tokenizer) throws CarrotException {
		do {
			String identifier = tokenizer.parseIdentifier();
			tokenizer.get(TokenType.ASSIGNMENT);
			Term expression = tokenizer.parseExpression();
			assignments.add(identifier, expression);
		} while (parser.tryConsume(TokenType.SEMICOLON));
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		scope.extendCurrent(assignments.create(engine.getConfig(), scope));
	}

}
