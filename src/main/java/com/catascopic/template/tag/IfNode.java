package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.Tokenizer;

public class IfNode implements Node {

	private final Term condition;

	public IfNode(Parser parser) throws CarrotException, IOException {
		Tokenizer tokenizer = parser.tokenizer();
		this.condition = tokenizer.parseExpression();
		tokenizer.end();
		Node next = parser.next();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope) throws CarrotException,
			IOException {
		// TODO Auto-generated method stub
		
	}

}
