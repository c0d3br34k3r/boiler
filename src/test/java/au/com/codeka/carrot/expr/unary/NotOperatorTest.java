package au.com.codeka.carrot.expr.unary;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

/**
 * @author Marten Gajda
 */
public class NotOperatorTest {
	
	@Test
	public void testApply() throws Exception {
		assertThat(UnaryOperator.NOT.apply(true)).isEqualTo(false);
		assertThat(UnaryOperator.NOT.apply(false)).isEqualTo(true);
		assertThat(UnaryOperator.NOT.apply(10.65)).isEqualTo(false);
		assertThat(UnaryOperator.NOT.apply(0)).isEqualTo(true);
		assertThat(UnaryOperator.NOT.apply("xyz")).isEqualTo(false);
		assertThat(UnaryOperator.NOT.apply("")).isEqualTo(true);
	}

	@Test
	public void testToString() throws Exception {
		assertThat(UnaryOperator.NOT.toString()).isEqualTo("NOT");
	}

}
