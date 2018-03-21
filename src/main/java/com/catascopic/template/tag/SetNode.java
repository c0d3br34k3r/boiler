package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.TokenType;
import com.catascopic.template.expr.Tokenizer;

/**
 * Set tag allows you to set the value a variable in the current scope to the
 * string value of the contents of the set block.
 */
public class SetNode implements Node {

	private BindingCreator assignments;

	SetNode(Tokenizer tokenizer) throws CarrotException {
		do {
			String identifier = tokenizer.parseIdentifier();
			tokenizer.consume(TokenType.ASSIGNMENT);
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
