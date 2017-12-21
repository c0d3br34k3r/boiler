package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;

public interface Node {

	void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException;

}
