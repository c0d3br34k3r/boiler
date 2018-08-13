package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;

public class Template {

	private final Node node;

	public Template(Reader reader) throws IOException {
		this.node = TemplateParser.parse(reader);
	}

	public void render(TemplateEngine engine, Path workingDirectory,
			Appendable writer, Map<String, Object> params) throws IOException {
		node.render(writer, new Scope(engine, workingDirectory, params));
	}

	public String render(TemplateEngine engine, Path workingDirectory,
			Map<String, Object> params) throws IOException {
		StringBuilder output = new StringBuilder();
		render(engine, workingDirectory, output, params);
		return output.toString();
	}

}
