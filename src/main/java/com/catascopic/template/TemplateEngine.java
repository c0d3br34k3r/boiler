package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;

public class TemplateEngine {

	private final ParseCache<Node> templateCache;
	private final ParseCache<String> textCache;
	private final Settings settings;

	private static final int DEFAULT_CACHE_SIZE = 64;

	public static TemplateEngine create() {
		return create(Settings.defaultSettings(), DEFAULT_CACHE_SIZE);
	}

	public static TemplateEngine create(Settings settings) {
		return create(settings, DEFAULT_CACHE_SIZE);
	}

	public static TemplateEngine create(Settings settings, int cacheSize) {
		return new TemplateEngine(settings, cacheSize);
	}

	private TemplateEngine(Settings settings, int cacheSize) {
		this.settings = settings;
		this.templateCache = new TemplateCache(cacheSize);
		this.textCache = new TextCache(cacheSize);
	}

	public void render(Path path, Appendable writer, Map<String, ? extends Object> params)
			throws IOException {
		templateCache.get(path).render(writer, new FileScope(path, this, params));
	}

	public String render(Path path, Map<String, Object> params) throws IOException {
		StringBuilder builder = new StringBuilder();
		render(path, builder, params);
		return builder.toString();
	}

	Node getTemplate(Path file) throws IOException {
		return templateCache.get(file);
	}

	String getTextFile(Path file) throws IOException {
		return textCache.get(file);
	}

	Settings settings() {
		return settings;
	}

	private static class TextCache extends ParseCache<String> {

		TextCache(int size) {
			super(size);
		}

		@Override
		protected String parse(Path file) throws IOException {
			return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
		}
	}

	private static class TemplateCache extends ParseCache<Node> {

		TemplateCache(int size) {
			super(size);
		}

		@Override
		protected Node parse(Path file) throws IOException {
			try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				return TemplateParser.parse(TrackingReader.create(reader, file));
			}
		}
	}

}
