package au.com.codeka.carrot.bindings;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author marten
 */
public class JsonObjectBindingsTest {
	
	private final static JsonObject JSON_OBJECT = new JsonParser().parse("{\n" +
			"  \"key1\": \"value\",\n" +
			"  \"key2\": 2,\n" +
			"  \"key3\": true,\n" +
			"  \"key4\": [\"a\", \"b\"],\n" +
			"  \"key5\": {\n" +
			"    \"inner\": \"value\"\n" +
			"  },\n" +
			"  \"key6\": null\n" +
			"}").getAsJsonObject();

	@Test
	public void testResolved() throws Exception {
		assertThat(new JsonObjectBindings(new JsonObject()).resolve("key"),
				CoreMatchers.nullValue());
		assertThat(new JsonObjectBindings(JSON_OBJECT).resolve("key1"),
				CoreMatchers.<Object> is("value"));
		assertThat(new JsonObjectBindings(JSON_OBJECT).resolve("key2"),
				CoreMatchers.<Object> is(2));
		assertThat(new JsonObjectBindings(JSON_OBJECT).resolve("key3"),
				CoreMatchers.<Object> is(true));
		assertThat(new JsonObjectBindings(JSON_OBJECT).resolve("key4"),
				CoreMatchers.instanceOf(JsonArrayBindings.class));
		assertThat(new JsonObjectBindings(JSON_OBJECT).resolve("key5"),
				CoreMatchers.instanceOf(JsonObjectBindings.class));
		assertThat(new JsonObjectBindings(JSON_OBJECT).resolve("key6"),
				CoreMatchers.nullValue());
	}

	@Test
	public void testIsEmpty() throws Exception {
		assertThat(new JsonObjectBindings(new JsonObject()).isEmpty(), CoreMatchers.is(true));
		assertThat(new JsonObjectBindings(JSON_OBJECT).isEmpty(),
				CoreMatchers.is(false));
	}
}
