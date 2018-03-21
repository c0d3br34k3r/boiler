package au.com.codeka.carrot.tag;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.catascopic.template.Bindings;
import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Configuration;
import com.catascopic.template.bindings.EmptyBindings;
import com.catascopic.template.resource.MemoryResourceLocator;

/**
 * Tests for {@link ExtendsTag} (and, by extension, {@link BlockTag}).
 */
@RunWith(JUnit4.class)
public class ExtendsTagTest {
	@Test
	public void testBasic() {
		CarrotEngine engine = createEngine(
				"skeleton", "Hello{% block \"foo\" %}blah blah{% end %}World",
				"index", "{% extends \"skeleton\" %}{% block \"foo\" %}yada yada{% end %}");
		String result = render(engine, "index", EmptyBindings.INSTANCE);
		assertThat(result).isEqualTo("Helloyada yadaWorld");
	}

	@Test
	public void testMultipleBlocks() {
		CarrotEngine engine = createEngine(
				"skeleton",
				"Hello{% block \"foo\" %}blah blah{% endblock %}World{% block \"bar\" %}{% endblock %}",
				"index",
				"{% extends \"skeleton\" %}{% block \"foo\" %}yada yada{% end %}{% block \"bar\" %}stuff{% endblock %}");
		String result = render(engine, "index", EmptyBindings.INSTANCE);
		assertThat(result).isEqualTo("Helloyada yadaWorldstuff");
	}

	@Test
	public void testMissingBlock() {
		CarrotEngine engine = createEngine(
				"skeleton", "Hello{% block \"foo\" %}blah blah{% end %}World",
				"index", "{% extends \"skeleton\" %}");
		String result = render(engine, "index", EmptyBindings.INSTANCE);
		assertThat(result).isEqualTo("Helloblah blahWorld");
	}

	@Test
	public void testSkeletonNotFound() {
		CarrotEngine engine = createEngine(
				"index", "{% extends \"skeleton\" %}");
		try {
			engine.process("index", EmptyBindings.INSTANCE);
			fail("Expected CarrotException.");
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo(
							"index\n1: {% extends \"skeleton\" %}\n                           ^\nFile not found: skeleton");
		}
	}

	private String render(CarrotEngine engine, String templateName, @Nullable Bindings bindings) {
		try {
			return engine.process(templateName, bindings);
		} catch (CarrotException e) {
			throw new RuntimeException(e);
		}
	}

	private CarrotEngine createEngine(String... nameValues) {
		CarrotEngine engine = new CarrotEngine(new Configuration.Builder()
				.setLogger(new Configuration.Logger() {
					@Override
					public void print(int level, String msg) {
						System.err.println(msg);
					}
				})
				.setResourceLocator(createResources(nameValues))
				.build());
		return engine;
	}

	private MemoryResourceLocator.Builder createResources(String... nameValues) {
		Map<String, String> resources = new TreeMap<>();
		for (int i = 0; i < nameValues.length; i += 2) {
			resources.put(nameValues[i], nameValues[i + 1]);
		}
		return new MemoryResourceLocator.Builder(resources);
	}
}
