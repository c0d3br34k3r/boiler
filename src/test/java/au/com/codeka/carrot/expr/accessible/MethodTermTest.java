package au.com.codeka.carrot.expr.accessible;

import static au.com.codeka.carrot.util.RenderHelper.render;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.bindings.EmptyBindings;
import au.com.codeka.carrot.expr.Term;

/**
 * @author Marten Gajda
 */
public class MethodTermTest {
	@Test
	public void testEvaluate() throws Exception {
		final Iterable<Object> testParams = new ArrayList<>();
		final Configuration testConfiguration = new Configuration.Builder().build();
		final Scope testScope = new Scope(new EmptyBindings());
		final Object testResult = new Object();

		assertThat(
				new MethodTerm(
						new AccessibleTerm() {
							@Nonnull
							@Override
							public Callable callable(@Nonnull Configuration config,
									@Nonnull Scope scope) throws CarrotException {
								assertThat(config).isSameAs(testConfiguration);
								assertThat(scope).isSameAs(testScope);
								return new Callable() {
									@Nullable
									@Override
									public Object call(@Nonnull Iterable<?> params)
											throws CarrotException {
										assertThat(params).isSameAs(testParams);
										return testResult;
									}
								};
							}

							@Override
							public Object evaluate(Configuration config, Scope scope)
									throws CarrotException {
								fail("Evaluate called");
								return null;
							}
						},
						new Term() {
							@Override
							public Object evaluate(Configuration config, Scope scope)
									throws CarrotException {
								assertThat(config).isSameAs(testConfiguration);
								assertThat(scope).isSameAs(testScope);
								return testParams;
							}
						}).evaluate(testConfiguration, testScope)).isSameAs(testResult);
	}

	@Test
	public void testNullParameters() throws CarrotException {
		final Object nullTester = new Object() {
			@SuppressWarnings("unused") // Used by the engine.
			public boolean isValueNull(String val) {
				return (val == null);
			}
		};

		assertThat(render("{% if obj.isValueNull(null) %}yep{% end %}",
				"obj", nullTester)).isEqualTo("yep");
		assertThat(render("{% if obj.isValueNull(foo) %}yep{% end %}",
				"obj", nullTester,
				"foo", null)).isEqualTo("yep");
		assertThat(render("{% if obj.isValueNull(foo) %}yep{% end %}",
				"obj", nullTester,
				"foo", "foo")).isEmpty();
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new MethodTerm(
				new AccessibleTerm() {
					@Nonnull
					@Override
					public Callable callable(@Nonnull Configuration config, @Nonnull Scope scope)
							throws CarrotException {
						fail("callable called");
						return null;
					}

					@Override
					public Object evaluate(Configuration config, Scope scope)
							throws CarrotException {
						fail("Evaluate called");
						return null;
					}

					@Override
					public String toString() {
						return "methodname";
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
						return "params";
					}
				}).toString()).isEqualTo("methodname LEFT_PAREN params RIGHT_PAREN");
	}

}
