package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.Tokenizer;

public class IncludeNode implements Node {

	private final Term fileName;
	
	public IncludeNode(Tokenizer tokenizer) throws CarrotException {
		fileName = tokenizer.parseExpression();
		tokenizer.end();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {

	}

}
