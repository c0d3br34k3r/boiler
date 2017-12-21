package au.com.codeka.carrot.bindings;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * @author marten
 */
public class EmptyBindingsTest {

	@Test
	public void testIterator() throws Exception {
		assertThat(EmptyBindings.INSTANCE.iterator().hasNext(), CoreMatchers.is(false));
	}

	@Test
	public void testResolved() throws Exception {
		assertThat(EmptyBindings.INSTANCE.resolve("abc"), CoreMatchers.nullValue());
	}

	@Test
	public void testIsEmpty() throws Exception {
		assertThat(EmptyBindings.INSTANCE.isEmpty(), CoreMatchers.is(true));
	}

}
