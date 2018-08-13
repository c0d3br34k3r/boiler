package com.catascopic.template.parse;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;

import org.junit.Test;

import com.catascopic.template.Scope;

public class TemplateParserTest {

	@Test
	public void test() throws IOException {
		String result = render("<% set a, b, c = '123' %>"
				+ "<% if a == 2 %>foo"
				+ "<% else if b == 2 %>bar"
				+ "<% else if c == 2 %>baz"
				+ "<% end %>"
				+ "what");
		System.out.println(result);
	}

	private static String render(String string) throws IOException {
		StringWriter writer = new StringWriter();
		Node document = TemplateParser.parse(new StringReader(string));
		System.out.println(document);
		document.render(writer,
				Scope.create(null, null, Collections
						.<String, Object> emptyMap()));
		return writer.toString();
	}

}
