package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.expr.Values;
import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;
import com.google.common.collect.ImmutableMap;

public abstract class TemplateResolver {

	private final ImmutableMap<String, TemplateFunction> functions;

	TemplateResolver(Map<String, TemplateFunction> functions) {
		this.functions = ImmutableMap.copyOf(functions);
	}

	TemplateFunction getFunction(String name) {
		TemplateFunction function = functions.get(name);
		if (function == null) {
			throw new TemplateEvalException("undefined function %s", name);
		}
		return function;
	}

	abstract Node getTemplate(Path file) throws IOException;

	abstract String getTextFile(Path file) throws IOException;

	static <F extends Enum<F> & TemplateFunction> void addFunctions(
			Map<String, TemplateFunction> functions,
			Class<F> functionEnum) {
		for (F function : functionEnum.getEnumConstants()) {
			functions.put(Values.separatorToCamel(
					function.name().toLowerCase()), function);
		}
	}

	private static final Map<String, TemplateFunction> BUILTIN;
	static {
		Map<String, TemplateFunction> builder = new HashMap<>();
		addFunctions(builder, Builtin.class);
		BUILTIN = ImmutableMap.copyOf(builder);
	}

	public static final TemplateResolver DEFAULT = new TemplateResolver(
			BUILTIN) {

		@Override
		Node getTemplate(Path file) throws IOException {
			try (Reader reader = Files.newBufferedReader(file,
					StandardCharsets.UTF_8)) {
				return TemplateParser.parse(reader);
			}
		}

		@Override
		String getTextFile(Path file) throws IOException {
			return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
		}
	};

}
