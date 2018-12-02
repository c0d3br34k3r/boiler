package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
		return parse(text, FunctionResolver.builtinOnly());
	}

	public static Template parse(Reader reader, FunctionResolver functions)
			throws IOException {
		return new Template(TemplateParser.parse(reader), functions);
	}

	public static Template parse(Reader reader) throws IOException {
		return parse(reader, FunctionResolver.builtinOnly());
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
