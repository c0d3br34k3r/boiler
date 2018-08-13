package com.catascopic.template;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class FunctionsTest {

	@Test
	public void testReader() throws IOException {
		LineReader reader = new LineReader(new StringReader("8888"));
		Assert.assertEquals('8', reader.next());
		Assert.assertEquals('8', reader.next());
		Assert.assertEquals('8', reader.next());
		reader.unread('7');
		Assert.assertEquals('7', reader.next());
		Assert.assertEquals('8', reader.next());
		Assert.assertFalse(reader.hasNext());
		reader.unread('6');
		Assert.assertTrue(reader.hasNext());
		Assert.assertEquals('6', reader.next());
		Assert.assertFalse(reader.hasNext());
	}

}
