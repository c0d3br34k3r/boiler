package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
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

	public void render(TemplateResolver resolver, Path workingDirectory,
			Appendable writer, Map<String, Object> params) throws IOException {
		node.render(writer, new Scope(resolver, workingDirectory, params));
	}

	public String render(TemplateResolver engine, Path workingDirectory,
			Map<String, Object> params) throws IOException {
		StringBuilder output = new StringBuilder();
		render(engine, workingDirectory, output, params);
		return output.toString();
	}

	public void render(Path workingDirectory, Appendable writer,
			Map<String, Object> params) throws IOException {
		render(TemplateResolver.DEFAULT, workingDirectory, writer, params);
	}

	public String render(Path workingDirectory, Map<String, Object> params)
			throws IOException {
		return render(TemplateResolver.DEFAULT, workingDirectory, params);
	}

	public void render(Appendable writer, Map<String, Object> params)
			throws IOException {
		render(Paths.get("."), writer, params);
	}

	public String render(Map<String, Object> params) throws IOException {
		return render(Paths.get("."), params);
	}

}
