package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;

public enum MarkerNode implements Node {

	END,
	END_DOCUMENT;

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		throw new UnsupportedOperationException();
	}

}
