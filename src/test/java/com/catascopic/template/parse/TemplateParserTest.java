package com.catascopic.template.parse;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import com.catascopic.template.Template;
import com.google.common.collect.ImmutableMap;

public class TemplateParserTest {

	@Test
	public void test() throws IOException {
		String text = "@{set i, j, k = '123'}\n"
				+ "title\n"
				+ "  @{if a == 2}  \n"
				+ "i(${i}): ${3 * a}\n"
				+ "  @{else if b == 2}  \n"
				+ "j(${j}): ${'*' * a * 4}\n"
				+ "  @{else if c == 2}\n"
				+ "k(${k}): baz\n"
				+ "@{else if c == 3}\n"
				+ "stop\n"
				+ "@{end}"
				+ "${uneval('99/9')}";
		System.out.println(text);
		List<Tag> document = TagParser.parse(new StringReader(text));
		System.out.println();
		System.out.println(document);
		Template template = Template.parse(text);
		System.out.println();
		System.out.println(template);
		System.out.println();
		System.out.println(template.render(ImmutableMap.of("a", 5, "b", 5, "c", 3)));
	}

}
