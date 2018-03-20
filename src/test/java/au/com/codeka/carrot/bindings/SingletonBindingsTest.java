package au.com.codeka.carrot.bindings;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import au.com.codeka.carrot.EntryBindings;

/**
 * @author marten
 */
public class SingletonBindingsTest {
	@Test
	public void testIterator() throws Exception {
		assertThat(new SingletonBindings("key", "value").iterator().hasNext(),
				CoreMatchers.is(true));
		assertThat(new SingletonBindings("key", "value").iterator().next(),
				CoreMatchers.instanceOf(EntryBindings.class));
	}

	@Test
	public void testResolved() throws Exception {
		assertThat(new SingletonBindings("key", "value").resolve("key"),
				CoreMatchers.<Object> is("value"));
		assertThat(new SingletonBindings("key", "value").resolve("otherkey"),
				CoreMatchers.nullValue());
	}

	@Test
	public void testIsEmpty() throws Exception {
		assertThat(new SingletonBindings("key", "value").isEmpty(), CoreMatchers.is(false));
	}
}
