package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.ParseCache.TemplateCache;
import com.catascopic.template.ParseCache.TextCache;
import com.catascopic.template.expr.Values;
import com.catascopic.template.parse.Node;
import com.google.common.collect.ImmutableMap;

public class TemplateEngine {

	private final ImmutableMap<String, TemplateFunction> functions;
	private ParseCache<Node> templateCache = new TemplateCache();
	private ParseCache<String> textCache = new TextCache();

	private TemplateEngine(Map<String, TemplateFunction> functions) {
		this.functions = ImmutableMap.copyOf(functions);
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

	TemplateFunction getFunction(String name) {
		return functions.get(name);
	}

	Node getTemplate(Path file) throws IOException {
		return templateCache.get(file);
	}

	String getTextFile(Path file) throws IOException {
		return textCache.get(file);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Map<String, TemplateFunction> functions = new HashMap<>();

		private Builder() {
			addFunctions(Builtin.class);
		}

		public <F extends Enum<F> & TemplateFunction> void addFunctions(
				Class<F> functionEnum) {
			for (F function : functionEnum.getEnumConstants()) {
				functions.put(Values.separatorToCamel(
						function.name().toLowerCase()), function);
			}
		}

		public void addFunction(String name, TemplateFunction function) {
			functions.put(name, function);
		}

		public TemplateEngine build() {
			return new TemplateEngine(functions);
		}
	}

}
