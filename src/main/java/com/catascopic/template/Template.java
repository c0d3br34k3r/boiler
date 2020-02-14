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
import com.google.common.collect.ImmutableMap;

public class Template {

	public static Template parse(String text, Settings settings) {
		try {
			return parse(new StringReader(text), settings);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static Template parse(String text) {
		return parse(text, Settings.DEFAULT);
	}

	public static Template parse(Reader reader, Settings settings)
			throws IOException {
		return new Template(TemplateParser.parse(TrackingReader.create(reader)), settings);
	}

	public static Template parse(Reader reader) throws IOException {
		return parse(reader, Settings.DEFAULT);
	}

	public static Template parse(Path file, Settings settings)
			throws IOException {
		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			return parse(reader, settings);
		}
	}

	public static Template parse(Path file) throws IOException {
		return parse(file, Settings.DEFAULT);
	}

	private final Node node;
	private final Settings settings;

	private Template(Node node, Settings settings) {
		this.node = node;
		this.settings = settings;
	}

	public String render() {
		return render(newScope(newScope(ImmutableMap.<String, Object> of())));
	}

	public String render(Map<String, ? extends Object> params) {
		return render(newScope(params));
	}

	public String render(LocalAccess params) {
		return render(newScope(params));
	}

	public void render(Appendable writer, Map<String, ? extends Object> params) throws IOException {
		node.render(writer, newScope(params));
	}

	public void render(Appendable writer, LocalAccess params) throws IOException {
		node.render(writer, newScope(params));
	}

	private Scope newScope(Map<String, ?> params) {
		return new BasicScope(params, settings);
	}

	private Scope newScope(LocalAccess params) {
		return new BasicScope(params, settings);
	}

	private String render(Scope scope) {
		StringBuilder builder = new StringBuilder();
		try {
			node.render(builder, scope);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return node.toString();
	}

}
