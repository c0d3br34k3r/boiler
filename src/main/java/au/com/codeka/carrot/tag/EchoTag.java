package au.com.codeka.carrot.tag;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.StatementParser;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.tmpl.TagNode;

/**
 * Echo tag just echos the results of its single parameter.
 */
public class EchoTag extends Tag {

	private Term expr;

	@Override
	public void parseStatement(StatementParser stmtParser) throws CarrotException {
		expr = stmtParser.parseExpression();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException, IOException {
		writer.write(expr.evaluate(engine.getConfig(), scope).toString());
	}

}
