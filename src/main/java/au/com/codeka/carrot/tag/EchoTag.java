package au.com.codeka.carrot.tag;

import java.io.IOException;
import java.io.Writer;

import com.google.common.html.HtmlEscapers;

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
		expr = stmtParser.parseTerm();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException, IOException {
		Object value = expr.evaluate(engine.getConfig(), scope);
		// TODO: configurable escaper
		if (engine.getConfig().getAutoEscape()) {
			value = HtmlEscapers.htmlEscaper().escape(value.toString());
		}
		writer.write(value.toString());
	}

}
