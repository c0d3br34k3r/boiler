package au.com.codeka.carrot.expr.unary;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import com.catascopic.template.expr.UnaryOperator;

/**
 * @author Marten Gajda
 */
public class PlusOperatorTest {

	@Test
	public void testApply() throws Exception {
		assertThat(UnaryOperator.PLUS.apply(1)).isEqualTo(1);
		assertThat(UnaryOperator.PLUS.apply(-1)).isEqualTo(-1);
		assertThat(UnaryOperator.PLUS.apply(10.65)).isEqualTo(10.65);
		assertThat(UnaryOperator.PLUS.apply(-10.13)).isEqualTo(-10.13);
	}

	@Test
	public void testToString() throws Exception {
		assertThat(UnaryOperator.PLUS.toString()).isEqualTo("PLUS");
	}

}
