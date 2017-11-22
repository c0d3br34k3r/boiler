package au.com.codeka.carrot.expr.unary;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author Marten Gajda
 */
public class MinusOperatorTest {
	
	@Test
	public void testApply() throws Exception {
		assertThat(UnaryOperators.MINUS.apply(1)).isEqualTo(-1);
		assertThat(UnaryOperators.MINUS.apply(-1)).isEqualTo(1);
		assertThat(UnaryOperators.MINUS.apply(10.65)).isEqualTo(-10.65);
		assertThat(UnaryOperators.MINUS.apply(-10.13)).isEqualTo(10.13);
	}

	@Test
	public void testToString() throws Exception {
		assertThat(UnaryOperators.MINUS.toString()).isEqualTo("MINUS");
	}

}
