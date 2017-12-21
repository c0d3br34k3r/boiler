package au.com.codeka.carrot.tag;

import static com.google.common.truth.Truth.assertThat;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.bindings.EmptyBindings;
import au.com.codeka.carrot.bindings.SingletonBindings;
import au.com.codeka.carrot.resource.MemoryResourceLocator;

/**
 * Tests for {@link IncludeTag}.
 */
@RunWith(JUnit4.class)
public class IncludeTagTest {
	@Test
	public void testBasic() {
		CarrotEngine engine = createEngine(
				"foo", "Hello World",
				"index", "Stuff{% include \"foo\" %}Blah");
		String result = render(engine, "index", EmptyBindings.INSTANCE);
		assertThat(result).isEqualTo("StuffHello WorldBlah");
	}

	@Test
	public void testContextValuePassedThrough() {
		CarrotEngine engine = createEngine(
				"foo", "Hello{{foo}}World",
				"index", "Stuff{% include \"foo\" %}Blah");
		String result = render(engine, "index", new SingletonBindings("foo", "Bar"));
		assertThat(result).isEqualTo("StuffHelloBarWorldBlah");
	}

	@Test
	public void testNewContextValueSingle() {
		CarrotEngine engine = createEngine(
				"foo", "Hello{{foo}}World",
				"index", "Stuff{% include \"foo\" foo = \"Bar\" %}Blah");
		String result = render(engine, "index", EmptyBindings.INSTANCE);
		assertThat(result).isEqualTo("StuffHelloBarWorldBlah");
	}

	@Test
	public void testNewContextValueMultiple() {
		CarrotEngine engine = createEngine(
				"foo", "Hello{{foo}}World{{bar}}",
				"index", "Stuff{% include \"foo\" foo, bar = (\"xyz\", 123) %}Blah");
		String result = render(engine, "index", EmptyBindings.INSTANCE);
		assertThat(result).isEqualTo("StuffHelloxyzWorld123Blah");
	}

	private String render(CarrotEngine engine, String templateName, @Nullable Bindings bindings) {
		try {
			return engine.process(templateName, bindings);
		} catch (CarrotException e) {
			throw new RuntimeException(e);
		}
	}

	private CarrotEngine createEngine(String... nameValues) {
		return new CarrotEngine(new Configuration.Builder()
				.setLogger(new Configuration.Logger() {
					@Override
					public void print(int level, String msg) {
						System.err.println(msg);
					}
				})
				.setResourceLocator(createResources(nameValues))
				.build());
	}

	private MemoryResourceLocator.Builder createResources(String... nameValues) {
		Map<String, String> resources = new TreeMap<>();
		for (int i = 0; i < nameValues.length; i += 2) {
			resources.put(nameValues[i], nameValues[i + 1]);
		}
		return new MemoryResourceLocator.Builder(resources);
	}
}
