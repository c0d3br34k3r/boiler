package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.Tokenizer;

class Echo implements Node {

	private final Term term;

	Echo(Tokenizer tokenizer) throws CarrotException {
		term = tokenizer.parseExpression();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		writer.write(term.evaluate(engine.getConfig(), scope).toString());
	}

}
