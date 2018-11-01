package com.catascopic.template.parse;

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;

import com.catascopic.template.Template;

public class TemplateParserTest {

	@Test
	public void test() throws IOException {
		String template = "<% set a, b, c = '123' %>\n"
				+ "<% if a == 2 %>\n"
				+ "<<3 * a>>\n"
				+ "<% else if b == 2 %>\n"
				+ "<<'*' * a * 4>>\n"
				+ "<% else if c == 2 %>\n"
				+ "baz\n"
				+ "<% end %>\n"
				+ "what";
		//System.out.println(template);
		String result = render(template);
		System.out.println(result);
	}

	private static String render(String string) throws IOException {
		Template document = Template.parse(string);
		System.out.println(document);
		String render = document.render(Collections.<String, Object> emptyMap());
		return render;
	}

}
