package com.catascopic.template.parse;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;

import com.catascopic.template.Template;

public class TemplateParserTest {

	@Test
	public void test() throws IOException {
		String result = render("<% set a, b, c = '123' %>"
				+ "<% if a == 2 %>"
				+ "<<3 * a>>"
				+ "<% else if b == 2 %>"
				+ "<<'*' * b * 4>>"
				+ "<% else if c == 2 %>"
				+ "baz"
				+ "<% end %>"
				+ "what");
		System.out.println(result);
	}

	private static String render(String string) throws IOException {
		Template document = Template.parse(string);
		System.out.println(document);
		String render = document.render(Collections.<String, Object> emptyMap());
		return render;
	}

}
