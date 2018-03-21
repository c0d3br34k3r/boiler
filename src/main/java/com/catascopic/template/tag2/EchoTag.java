package com.catascopic.template.tag2;

import java.io.IOException;
import java.io.Writer;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.tag.TagNode;

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
