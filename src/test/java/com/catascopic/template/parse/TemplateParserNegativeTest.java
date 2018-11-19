package com.catascopic.template.parse;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class TemplateParserNegativeTest {

	@Test
	public void test() throws IOException {
		TemplateParser.parse(new StringReader("<%else%>haha"));
	}
}
