package au.com.codeka.carrot.expr.accessible;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.catascopic.template.bindings.Composite;
import com.catascopic.template.bindings.SingletonBindings;
import com.google.common.collect.ImmutableMap;

import au.com.codeka.carrot.util.MockLazyTerm;

/**
 * @author Marten Gajda
 */
public class AccessOperatorTest {
	@Test
	public void testApply() throws Exception {
		assertThat(new AccessOperator().apply(new String[] { "a", "b", "c" }, new MockLazyTerm(1)))
				.isEqualTo("b");
		assertThat(new AccessOperator().apply(Arrays.asList("a", "b", "c"), new MockLazyTerm(1)))
				.isEqualTo("b");
		assertThat(new AccessOperator().apply(ImmutableMap.of(1, "a", 2, "b", 3, "c"),
				new MockLazyTerm(2))).isEqualTo("b");
		assertThat(
				new AccessOperator().apply(
						new Composite(new SingletonBindings("1", "a"),
								new SingletonBindings("2", "b"), new SingletonBindings("3", "c")),
						new MockLazyTerm("2"))).isEqualTo("b");
		assertThat(new AccessOperator().apply(new FieldTestClass(), new MockLazyTerm("b")))
				.isEqualTo("B");
		assertThat(new AccessOperator().apply(new MethodTestClass(), new MockLazyTerm("a")))
				.isEqualTo("A");
		assertThat(new AccessOperator().apply(new MethodTestClass(), new MockLazyTerm("b")))
				.isEqualTo("B");
		assertThat(
				new AccessOperator().apply(Arrays.asList("a", "b", "c"), new MockLazyTerm(0)))
						.isEqualTo("a");
		assertThat(new AccessOperator().apply(Arrays.asList("a", "b", "c"),
				new MockLazyTerm(1L))).isEqualTo("b");
		assertThat(new AccessOperator().apply(Arrays.asList("a", "b", "c"),
				new MockLazyTerm(2L))).isEqualTo("c");
	}

	@Test
	public void testNull() throws Exception {
		assertThat(new AccessOperator().apply(null, new MockLazyTerm(0))).isNull();
	}

	private final class FieldTestClass {
		public final String a = "A";
		public final String b = "B";
		public final String c = "C";
	}

	private final class MethodTestClass {
		public String getA() {
			return "A";
		}

		public String b() {
			return "B";
		}
	}
}
