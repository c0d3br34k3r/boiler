package au.com.codeka.carrot.tag;

import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.StatementParser;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.tmpl.TagNode;

/**
 * Set tag allows you to set the value a variable in the current scope to the
 * string value of the contents of the set block.
 */
public class SetTag extends Tag {

	private BindingCreator assignments;

	@Override
	public boolean isBlockTag() {
		return false;
	}

	@Override
	public void parseStatement(StatementParser parser) throws CarrotException {
		do {
			String identifier = parser.parseIdentifier();
			parser.get(TokenType.ASSIGNMENT);
			Term expression = parser.parseExpression();
			assignments.add(identifier, expression);
		} while (parser.tryConsume(TokenType.SEMICOLON));
		parser.parseEnd();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException {
		// TODO: block-style set?
		scope.extendCurrent(assignments.create(engine.getConfig(), scope));
	}

}
