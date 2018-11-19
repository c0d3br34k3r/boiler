package com.catascopic.template.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.catascopic.template.Template;
import com.google.common.collect.ImmutableMap;

public class TemplateParserTest2 {

	@Test
	public void test() throws IOException {
		String text = new String(Files.readAllBytes(Paths.get("test.template")),
				StandardCharsets.UTF_8);
		Template template = Template.parse(text);
		System.out.println(template);
		System.out.println("BEGIN TEMPLATE");
		System.out.println(template.render(ImmutableMap.of("items",
				"foo/bar/baz/qux/quux")));
		System.out.println("END TEMPLATE");
	}

}
