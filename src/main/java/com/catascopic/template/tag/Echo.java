package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;

class Echo implements Node {

	private final Term term;

	Echo(Parser parser) throws CarrotException {
		this(parser.tokenizer());
	}

	Echo(Tokenizer tokenizer) throws CarrotException {
		term = tokenizer.parseExpression();
		tokenizer.end();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		writer.write(term.evaluate(engine.getConfig(), scope).toString());
	}

}
