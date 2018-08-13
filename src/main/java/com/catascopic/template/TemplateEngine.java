package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.ParseCache.TemplateCache;
import com.catascopic.template.ParseCache.TextCache;
import com.catascopic.template.parse.Node;

public class TemplateEngine extends TemplateResolver {

	private ParseCache<Node> templateCache = new TemplateCache();
	private ParseCache<String> textCache = new TextCache();

	TemplateEngine(Map<String, TemplateFunction> functions) {
		super(functions);
	}

	public void render(Path file, Appendable writer,
			Map<String, Object> params) throws IOException {
		templateCache.get(file).render(writer, new Scope(
				this, file.getParent(), params));
	}

	public String render(Path file, Map<String, Object> params)
			throws IOException {
		StringBuilder output = new StringBuilder();
		render(file, output, params);
		return output.toString();
	}

	@Override
	Node getTemplate(Path file) throws IOException {
		return templateCache.get(file);
	}

	@Override
	String getTextFile(Path file) throws IOException {
		return textCache.get(file);
	}

	public static class Builder {

		private Map<String, TemplateFunction> functions = new HashMap<>();

		private Builder() {
			addFunctions(Builtin.class);
		}

		private <F extends Enum<F> & TemplateFunction> void addFunctions(
				Class<F> functionEnum) {
			TemplateResolver.addFunctions(functions, functionEnum);
		}

		public void addFunction(String name, TemplateFunction function) {
			functions.put(name, function);
		}

		public TemplateEngine build() {
			return new TemplateEngine(functions);
		}
	}

}
