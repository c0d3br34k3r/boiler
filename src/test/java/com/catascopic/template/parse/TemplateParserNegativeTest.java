package com.catascopic.template.parse;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import com.catascopic.template.TemplateEvalException;

public class TemplateParserNegativeTest {

	@Test(expected = TemplateEvalException.class)
	public void test() throws IOException {
		TemplateParser.parse(new StringReader("<%else%>haha"));
	}
}
