package au.com.codeka.carrot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import au.com.codeka.carrot.bindings.MapBindings;
import au.com.codeka.carrot.resource.FileResourceLocator;

public class Test {

	public static void main(String[] args) throws CarrotException {
		CarrotEngine engine = new CarrotEngine(new Configuration.Builder()
				.setResourceLocator(
						new FileResourceLocator.Builder("C:\\users\\mkoren\\Documents\\carrot"))
				.build());

		Bindings bindings = new MapBindings(ImmutableMap.<String, Object> of("className", "Thing",
				"fields", ImmutableList.of(ImmutableMap.<String, Object> of("name", "field1",
						"nameUpper", "Field1", "type", "Foobar"))));
		System.out.println(engine.process("test.java", bindings));
	}

}
