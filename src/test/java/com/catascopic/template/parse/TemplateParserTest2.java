package com.catascopic.template.parse;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import com.catascopic.template.Template;
import com.catascopic.template.value.Values;

public class TemplateParserTest2 {

	@Test
	public void test() throws IOException {
		System.out.print(Template.parse(Paths.get("sum.template")).render());
	}

}
