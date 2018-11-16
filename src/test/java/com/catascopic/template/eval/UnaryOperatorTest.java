package com.catascopic.template.eval;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.catascopic.template.eval.UnaryOperator;

public class UnaryOperatorTest {

	@Test
	public void testNegative() throws Exception {
		assertEquals(-1, UnaryOperator.NEGATIVE.apply(1));
		assertEquals(1, UnaryOperator.NEGATIVE.apply(-1));
		assertEquals(-10.65, UnaryOperator.NEGATIVE.apply(10.65));
		assertEquals(10.13, UnaryOperator.NEGATIVE.apply(-10.13));
		assertEquals(-1, UnaryOperator.NEGATIVE.apply(true));
	}

	@Test
	public void testNot() throws Exception {
		assertEquals(false, UnaryOperator.NOT.apply(true));
		assertEquals(true, UnaryOperator.NOT.apply(false));
		assertEquals(false, UnaryOperator.NOT.apply(10.65));
		assertEquals(true, UnaryOperator.NOT.apply(0));
		assertEquals(false, UnaryOperator.NOT.apply("xyz"));
		assertEquals(true, UnaryOperator.NOT.apply(""));
		assertEquals(false, UnaryOperator.NOT.apply(1));
	}

	@Test
	public void testApply() throws Exception {
		assertEquals(1, UnaryOperator.POSITIVE.apply(1));
		assertEquals(-1, UnaryOperator.POSITIVE.apply(-1));
		assertEquals(10.65, UnaryOperator.POSITIVE.apply(10.65));
		assertEquals(-10.13, UnaryOperator.POSITIVE.apply(-10.13));
		assertEquals(0, UnaryOperator.POSITIVE.apply(false));
	}

}
