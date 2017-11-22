package au.com.codeka.carrot.expr.unary;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

/**
 * @author Marten Gajda
 */
public class NotOperatorTest {
	
	@Test
	public void testApply() throws Exception {
		assertThat(UnaryOperators.NOT.apply(true)).isEqualTo(false);
		assertThat(UnaryOperators.NOT.apply(false)).isEqualTo(true);
		assertThat(UnaryOperators.NOT.apply(10.65)).isEqualTo(false);
		assertThat(UnaryOperators.NOT.apply(0)).isEqualTo(true);
		assertThat(UnaryOperators.NOT.apply("xyz")).isEqualTo(false);
		assertThat(UnaryOperators.NOT.apply("")).isEqualTo(true);
	}

	@Test
	public void testToString() throws Exception {
		assertThat(UnaryOperators.NOT.toString()).isEqualTo("NOT");
	}

}
