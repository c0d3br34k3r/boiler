package au.com.codeka.carrot.bindings;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * @author marten
 */
public class JsonArrayBindingsTest {
	private final static JsonArray JSON_ARRAY = new JsonParser().parse("[\n" +
			"  \"value\",\n" +
			"  2,\n" +
			"  true,\n" +
			"  [\n" +
			"    \"a\",\n" +
			"    \"b\"\n" +
			"  ],\n" +
			"  {\n" +
			"    \"inner\": \"value\"\n" +
			"  },\n" +
			"  null\n" +
			"]").getAsJsonArray();

	@Test
	public void testResolved() throws Exception {
		assertThat(new JsonArrayBindings(JSON_ARRAY).resolve("0"),
				CoreMatchers.<Object> is("value"));
		assertThat(new JsonArrayBindings(JSON_ARRAY)
				.resolve("1"),
				CoreMatchers.<Object> is(2));
		assertThat(new JsonArrayBindings(JSON_ARRAY)
				.resolve("2"),
				CoreMatchers.<Object> is(true));
		assertThat(new JsonArrayBindings(JSON_ARRAY)
				.resolve("3"),
				CoreMatchers.instanceOf(JsonArrayBindings.class));
		assertThat(new JsonArrayBindings(JSON_ARRAY)
				.resolve("4"),
				CoreMatchers.instanceOf(JsonObjectBindings.class));
		assertThat(new JsonArrayBindings(JSON_ARRAY)
				.resolve("5"),
				CoreMatchers.nullValue());
	}

	@Test
	public void testIsEmpty() throws Exception {
		assertThat(new JsonArrayBindings(new JsonArray()).isEmpty(), CoreMatchers.is(true));
		assertThat(new JsonArrayBindings(JSON_ARRAY)
				.isEmpty(),
				CoreMatchers.is(false));
	}
}
