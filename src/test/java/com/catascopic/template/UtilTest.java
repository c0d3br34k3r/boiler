package com.catascopic.template;

import org.junit.Assert;
import org.junit.Test;

import com.catascopic.template.Values;

public class UtilTest {

	@Test
	public void addTest() {
		Assert.assertEquals(5, Values.add(2, 3));
		Assert.assertEquals(5.5, Values.add(2.5, 3));
		Assert.assertEquals(7, Values.add("3", 4));
		Assert.assertEquals(7, Values.add(3, "4"));
		Assert.assertEquals(7.5, Values.add("3.5", 4));
		Assert.assertEquals(7.5, Values.add("3", 4.5));
		Assert.assertEquals("34", Values.add("3", "4"));
		Assert.assertEquals("q1", Values.add("q", 1));
		Assert.assertEquals("q1", Values.add("q", "1"));
		Assert.assertEquals("5r", Values.add(5, "r"));
		Assert.assertEquals("ab", Values.add("a", "b"));
		Assert.assertEquals(2, Values.add(1, true));
		Assert.assertEquals("1true", Values.add("1", true));
		Assert.assertEquals(4, Values.add(false, 4));
		Assert.assertEquals("falsey", Values.add(false, "y"));
		Assert.assertEquals("truetrue", Values.add(true, true));
	}

}
