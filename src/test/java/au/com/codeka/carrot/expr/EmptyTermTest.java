package au.com.codeka.carrot.expr;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.bindings.EmptyBindings;

/**
 * @author Marten Gajda
 */
public class EmptyTermTest {
	
	@Test
	public void testEvaluate() throws Exception {
		assertThat(((Iterable<?>) EmptyTerm.INSTANCE.evaluate(
				new Configuration.Builder().build(),
				new Scope(EmptyBindings.INSTANCE))).iterator().hasNext()).isFalse();
	}

	@Test
	public void testToString() throws Exception {
		assertThat(EmptyTerm.INSTANCE.toString()).isEqualTo("");
	}

}
