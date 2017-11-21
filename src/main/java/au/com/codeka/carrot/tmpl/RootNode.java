package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.resource.ResourcePointer;

/**
 * Special node that represents the root of the syntax tree.
 */
public class RootNode extends Node {
	public RootNode(ResourcePointer ptr) {
		super(ptr, true /* isBlockNode */);
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		renderChildren(engine, writer, scope);
	}
}
