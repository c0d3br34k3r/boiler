package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;

public class Template {

	public static Template parse(String text, FunctionResolver functions) {
		try {
			return parse(new StringReader(text), functions);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static Template parse(String text) {
		return parse(text, FunctionResolver.builtInOnly());
	}

	public static Template parse(Reader reader, FunctionResolver functions)
			throws IOException {
		return new Template(TemplateParser.parse(reader), functions);
	}

	public static Template parse(Reader reader) throws IOException {
		return parse(reader, FunctionResolver.builtInOnly());
	}

	public static Template parse(Path file, FunctionResolver functions)
			throws IOException {
		try (Reader reader =
				Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			return parse(reader, functions);
		}
	}

	public static Template parse(Path file) throws IOException {
		return parse(file, FunctionResolver.builtInOnly());
	}

	private final Node node;
	private final FunctionResolver functions;

	private Template(Node node, FunctionResolver functions) {
		this.node = node;
		this.functions = functions;
	}

	public String render(Map<String, ? extends Object> params) {
		StringBuilder builder = new StringBuilder();
		try {
			render(builder, params);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return builder.toString();
	}

	public void render(Appendable writer, Map<String, ? extends Object> params)
			throws IOException {
		node.render(writer, new BasicScope(params, functions));
	}

	@Override
	public String toString() {
		return node.toString();
	}

}
