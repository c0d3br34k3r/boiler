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

	public static Template parse(String text, Settings settings) {
		try {
			return parse(new StringReader(text), settings);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static Template parse(String text) {
		return parse(text, Settings.defaultSettings());
	}

	public static Template parse(Reader reader, Settings settings)
			throws IOException {
		return new Template(TemplateParser.parse(TrackingReader.create(reader)), settings);
	}

	public static Template parse(Reader reader) throws IOException {
		return parse(reader, Settings.defaultSettings());
	}

	public static Template parse(Path file, Settings settings)
			throws IOException {
		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			return parse(reader, settings);
		}
	}

	public static Template parse(Path file) throws IOException {
		return parse(file, Settings.defaultSettings());
	}

	private final Node node;
	private final Settings settings;

	private Template(Node node, Settings settings) {
		this.node = node;
		this.settings = settings;
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

	public void render(Appendable writer, Map<String, ? extends Object> params) throws IOException {
		node.render(writer, new BasicScope(params, settings));
	}

	@Override
	public String toString() {
		return node.toString();
	}

}
