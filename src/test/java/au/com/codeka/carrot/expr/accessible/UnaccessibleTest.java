package au.com.codeka.carrot.expr.accessible;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.util.HashSet;

import org.junit.Test;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.bindings.EmptyBindings;
import au.com.codeka.carrot.expr.Term;

/**
 * @author Marten Gajda
 */
public class UnaccessibleTest {

	@Test
	public void testEvaluate() throws Exception {
		final Configuration testConfiguration = new Configuration.Builder().build();
		final Scope testScope = new Scope(EmptyBindings.INSTANCE);
		final Object testResult = new Object();

		assertThat(new Unaccessible(new Term() {
			@Override
			public Object evaluate(Configuration config, Scope scope) throws CarrotException {
				assertThat(config).isSameAs(testConfiguration);
				assertThat(scope).isSameAs(testScope);
				return testResult;
			}
		}).evaluate(testConfiguration, testScope)).isSameAs(testResult);
	}

	@Test(expected = CarrotException.class)
	public void testCallable() throws Exception {
		new Unaccessible(new Term() {
			@Override
			public Object evaluate(Configuration config, Scope scope) throws CarrotException {
				fail("evaluate called");
				return null;
			}
		}).callable(new Configuration.Builder().build(), new Scope(EmptyBindings.INSTANCE))
				.call(new HashSet<>());
	}

}
