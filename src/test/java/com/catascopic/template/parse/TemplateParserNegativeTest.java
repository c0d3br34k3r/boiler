package com.catascopic.template.parse;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

import org.junit.Test;

import com.catascopic.template.TemplateParseException;
import com.catascopic.template.TestUtil;
import com.catascopic.template.TrackingReader;

public class TemplateParserNegativeTest {

	@Test(expected = TemplateParseException.class)
	public void test() throws IOException {
		TemplateParser.parse(TrackingReader.create(new StringReader("what\n@{else}\nhaha\n@{end}"))).render(
				System.out, TestUtil.testScope(Collections.<String, Object> emptyMap()));
	}
}
