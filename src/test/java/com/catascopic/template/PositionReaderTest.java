package com.catascopic.template;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

public class PositionReaderTest {

	@Test
	public void testReader() throws IOException {
		TrackingReader reader = new TrackingReader(new StringReader("8888"));
		Assert.assertEquals('8', reader.read());
		Assert.assertEquals('8', reader.read());
		Assert.assertEquals('8', reader.read());
		Assert.assertEquals(3, reader.getLocation().column());
		reader.unread('7');
		Assert.assertEquals('7', reader.read());
		Assert.assertEquals('8', reader.read());
		Assert.assertEquals(-1, reader.read());
		reader.unread('6');
		Assert.assertEquals('6', reader.read());
		Assert.assertEquals(-1, reader.read());
		Assert.assertEquals(4, reader.getLocation().column());
	}

}
