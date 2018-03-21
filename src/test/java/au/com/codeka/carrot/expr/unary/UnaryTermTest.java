package au.com.codeka.carrot.expr.unary;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.catascopic.template.CarrotException;
import com.catascopic.template.Configuration;
import com.catascopic.template.Scope;
import com.catascopic.template.bindings.EmptyBindings;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.UnaryOperator;
import com.catascopic.template.expr.UnaryTerm;

/**
 * @author Marten Gajda
 */
public class UnaryTermTest {
	@Test
	public void testEvaluate() throws Exception {
		final Configuration testConfiguration = new Configuration.Builder().build();
		final Scope testScope = new Scope(EmptyBindings.INSTANCE);
		final Object testValue = new Object();
		final Object testResult = new Object();

		assertThat(new UnaryTerm(new UnaryOperator() {
			@Override
			public Object apply(Object value) throws CarrotException {
				assertThat(value).isSameAs(testValue);
				return testResult;
			}
		}, new Term() {
			@Override
			public Object evaluate(Configuration config, Scope scope) throws CarrotException {
				assertThat(config).isSameAs(testConfiguration);
				assertThat(scope).isSameAs(testScope);
				return testValue;
			}
		}).evaluate(testConfiguration, testScope)).isSameAs(testResult);
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new UnaryTerm(
				new UnaryOperator() {
					@Override
					public Object apply(Object value) throws CarrotException {
						fail("apply called");
						return null;
					}

					@Override
					public String toString() {
						return "operator";
					}
				},
				new Term() {
					@Override
					public Object evaluate(Configuration config, Scope scope)
							throws CarrotException {
						fail("Evaluate called");
						return null;
					}

					@Override
					public String toString() {
						return "value";
					}
				}).toString()).isEqualTo("operator value");
	}

}
