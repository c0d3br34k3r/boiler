package au.com.codeka.carrot.expr;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import static au.com.codeka.carrot.expr.Functions.*;

public class FunctionsTest {

	@Test
	public void testSeparatorToCamel() {
		Assert.assertEquals(camelToSeparator(""), "");
		Assert.assertEquals(camelToSeparator("foo"), "foo");
		Assert.assertEquals(camelToSeparator("fooBar"), "foo_bar");
		Assert.assertEquals(camelToSeparator("fooBarBaz"),
				"foo_bar_baz");
	}

	@Test
	public void testCamelToSeparator() {
		Assert.assertEquals(separatorToCamel(""), "");
		Assert.assertEquals(separatorToCamel("foo"), "foo");
		Assert.assertEquals(separatorToCamel("foo_bar"), "fooBar");
		Assert.assertEquals(separatorToCamel("foo_bar_baz"), "fooBarBaz");
	}

	@Test
	public void testRange() {
		Assert.assertEquals(range(5), Arrays.asList(0, 1, 2, 3, 4));
		Assert.assertEquals(range(3, 6), Arrays.asList(3, 4, 5));
		Assert.assertEquals(range(2, 0), Collections.emptyList());
		Assert.assertEquals(range(0, 2, -1), Collections.emptyList());
		Assert.assertEquals(range(2, 0, -1), Arrays.asList(2, 1));
		Assert.assertEquals(range(0, 30, 5), Arrays.asList(0, 5, 10, 15, 20, 25));
	}

	@Test
	public void testSlice() {
		Assert.assertEquals(slice("abcde", 0, 2, 1), "ab");
		Assert.assertEquals(slice("abcde", null, 2), "ab");
		Assert.assertEquals(slice("abcde", 5, 5, 1), "");
		Assert.assertEquals(slice("abcde", -1, 0, -1), "edcb");
		Assert.assertEquals(slice("abcde", -1, -6, -1), "edcba");
		Assert.assertEquals(slice("abcde", null, null, -1), "edcba");
		Assert.assertEquals(slice("abcde", null, null, null), "abcde");
		Assert.assertEquals(slice("abcde", null, null, 2), "ace");
		Assert.assertEquals(slice("abcde", null, null, -2), "eca");
		Assert.assertEquals(slice("abcde", -2, null, -2), "db");
		Assert.assertEquals(slice("abcde", -2), "de");
	}

}
