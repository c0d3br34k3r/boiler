package com.catascopic.template.parse;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

public class TemplateParserTest {

	@Test
	public void test() throws IOException {
		String template = "<% set a, b,  c = '123' %>\n"
				+ "  <% if a == 2 %>  \n"
				+ "<<3 * a>>\n"
				+ "  <% else if b == 2 %>  \n"
				+ "  <<'*' * a * 4>>\n"
				+ "  <% else if c == 2 %>\n"
				+ " baz\n"
				+ "<% end %>  \n"
				+ "what";
		System.out.println(template);
		render(template);
	}

	private static void render(String string) throws IOException {
		List<Tag> document = TemplateParser.parse(new StringReader(string));
		System.out.println(document);
	}

}
