package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		render((TemplateResolver) engine, workingDirectory, writer, params);
	}

	public String render(TemplateEngine engine, Path workingDirectory,
			Map<String, Object> params) throws IOException {
		return render((TemplateResolver) engine, workingDirectory, params);
	}

	private void render(TemplateResolver resolver, Path workingDirectory,
			Appendable writer, Map<String, Object> params) throws IOException {
		node.render(writer, new Scope(resolver, workingDirectory, params));
	}

	private String render(TemplateResolver engine, Path workingDirectory,
			Map<String, Object> params) throws IOException {
		StringBuilder output = new StringBuilder();
		render(engine, workingDirectory, output, params);
		return output.toString();
	}

	public void render(Appendable writer, Map<String, Object> params)
			throws IOException {
		render(TemplateResolver.DEFAULT, Paths.get("."), writer, params);
	}

	public String render(Map<String, Object> params) throws IOException {
		return render(TemplateResolver.DEFAULT, Paths.get("."), params);
	}

}
