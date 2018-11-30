package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;

public class Template {

	public static Template parse(String text) {
		try {
			return parse(new StringReader(text));
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static Template parse(Reader reader) throws IOException {
		return new Template(TemplateParser.parse(reader));
	}

	private final Node node;
	private final FunctionResolver functions;

	private Template(Node node) {
		this.node = node;
		functions = FunctionResolver.builtinOnly();
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
		Scope scope = new BasicScope(params, functions);
		node.render(writer, scope);
	}

	@Override
	public String toString() {
		return node.toString();
	}

}
