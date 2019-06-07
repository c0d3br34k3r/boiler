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
		return create(Settings.DEFAULT, DEFAULT_CACHE_SIZE);
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

	public void render(Path path, Appendable writer, Map<String, ?> params)
			throws IOException {
		render(path, writer, newScope(path, params));
	}

	public void render(Path path, Appendable writer, LocalAccess params)
			throws IOException {
		render(path, writer, newScope(path, params));
	}

	public String render(Path path, Map<String, ?> params) {
		return render(path, newScope(path, params));
	}

	public String render(Path path, LocalAccess params) {
		return render(path, newScope(path, params));
	}

	private Scope newScope(Path path, Map<String, ?> params) {
		return new FileScope(path, this, params);
	}

	private Scope newScope(Path path, LocalAccess params) {
		return new FileScope(path, this, params);
	}

	private String render(Path path, Scope scope) {
		StringBuilder builder = new StringBuilder();
		try {
			render(path, builder, scope);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return builder.toString();
	}

	private void render(Path path, Appendable writer, Scope scope) throws IOException {
		getTemplate(path).render(writer, scope);
	}

	Node getTemplate(Path file) {
		try {
			return templateCache.get(file);
		} catch (IOException e) {
			throw new TemplateRenderException(e);
		}
	}

	String getTextFile(Path file) {
		try {
			return textCache.get(file);
		} catch (IOException e) {
			throw new TemplateRenderException(e);
		}
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
