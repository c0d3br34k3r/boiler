package au.com.codeka.carrot.bindings;

import static org.junit.Assert.assertThat;

import java.util.AbstractMap;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import au.com.codeka.carrot.EntryBindings;

/**
 * @author Marten Gajda
 */
public class EntryBindingsTest {
	@Test
	public void testResolve() throws Exception {
		assertThat(new EntryBindings(
				new AbstractMap.SimpleImmutableEntry<String, Object>("entry_key", "entry_value"))
						.resolve("key"),
				CoreMatchers.<Object> is("entry_key"));
		assertThat(new EntryBindings(
				new AbstractMap.SimpleImmutableEntry<String, Object>("entry_key", "entry_value"))
						.resolve("value"),
				CoreMatchers.<Object> is("entry_value"));
		assertThat(new EntryBindings(
				new AbstractMap.SimpleImmutableEntry<String, Object>("entry_key", "entry_value"))
						.resolve("other"),
				CoreMatchers.nullValue());
	}

	@Test
	public void testIsEmpty() throws Exception {
		assertThat(new EntryBindings(
				new AbstractMap.SimpleImmutableEntry<String, Object>("entry_key", "entry_value"))
						.isEmpty(),
				CoreMatchers.is(false));
	}
}
