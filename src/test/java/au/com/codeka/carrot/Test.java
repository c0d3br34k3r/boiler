package au.com.codeka.carrot;

import au.com.codeka.carrot.bindings.EmptyBindings;
import au.com.codeka.carrot.resource.FileResourceLocator;

public class Test {

	public static void main(String[] args) throws CarrotException {
		CarrotEngine engine = new CarrotEngine(new Configuration.Builder()
				.setResourceLocator(
						new FileResourceLocator.Builder("C:\\users\\mkoren\\Documents\\carrot"))
				.build());

		System.out.println(engine.process("test.txt", new EmptyBindings()));
	}

}
