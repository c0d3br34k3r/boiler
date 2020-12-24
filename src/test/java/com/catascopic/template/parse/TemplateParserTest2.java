package com.catascopic.template.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.catascopic.template.Template;

public class TemplateParserTest2 {

	@Test
	public void test() throws IOException {
		Files.write(Paths.get("x.svg"), Template.parse(Paths.get("x.template")).render().getBytes(StandardCharsets.UTF_8));
	}
	
}
