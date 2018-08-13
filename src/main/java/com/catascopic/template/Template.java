package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Map;

import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;

public class Template {

	private final Node node;

	public Template(Reader reader) throws IOException {
		this.node = TemplateParser.parse(reader);
	}

	public Template(String text) throws IOException {
		this(new StringReader(text));
	}

	public void render(TemplateEngine engine, Path workingDirectory,
			Appendable writer, Map<String, Object> params) throws IOException {
		renderInternal(engine, workingDirectory, writer, params);
	}

	public String render(TemplateEngine engine, Path workingDirectory,
			Map<String, Object> params) throws IOException {
		return renderInternal(engine, workingDirectory, params);
	}

	public void render(Appendable writer, Map<String, Object> params)
			throws IOException {
		renderInternal(TemplateResolver.DEFAULT, null, writer, params);
	}

	public String render(Map<String, Object> params) throws IOException {
		return renderInternal(TemplateResolver.DEFAULT, null, params);
	}

	private void renderInternal(TemplateResolver resolver,
			Path workingDirectory,
			Appendable writer, Map<String, Object> params) throws IOException {
		node.render(writer, new Scope(resolver, workingDirectory, params));
	}

	private String renderInternal(TemplateResolver engine,
			Path workingDirectory,
			Map<String, Object> params) throws IOException {
		StringBuilder output = new StringBuilder();
		renderInternal(engine, workingDirectory, output, params);
		return output.toString();
	}

}
