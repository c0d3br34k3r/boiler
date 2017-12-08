package au.com.codeka.carrot;

import com.google.common.collect.ImmutableMap;

import au.com.codeka.carrot.bindings.MapBindings;
import au.com.codeka.carrot.resource.FileResourceLocator;

public class Test {

	public static void main(String[] args) throws CarrotException {

		System.out.println(engine.process("test.txt", new MapBindings(
				ImmutableMap.<String, Object> of("trashedCard", "Estate"))));
	}

}
