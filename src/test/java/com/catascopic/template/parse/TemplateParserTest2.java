package com.catascopic.template.parse;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import com.catascopic.template.TemplateEngine;
import com.google.common.collect.ImmutableMap;

public class TemplateParserTest2 {

	@Test
	public void test() throws IOException {
		TemplateEngine engine = TemplateEngine.create();
		System.out.println("BEGIN TEMPLATE");
		System.out.println(engine.render(Paths.get("test.template"),
				ImmutableMap.<String, Object> of("items",
						"foo/bar/baz/qux/quux")));
		System.out.println("END TEMPLATE");
	}

}
