package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.expr.Values;
import com.catascopic.template.parse.Node;
import com.google.common.collect.ImmutableMap;

abstract class TemplateResolver {

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

	private static Map<String, TemplateFunction> builtinOnly() {
		Map<String, TemplateFunction> funtions = new HashMap<>();
		addFunctions(funtions, Builtin.class);
		return funtions;
	}

	static final TemplateResolver DEFAULT = new TemplateResolver(
			builtinOnly()) {

		@Override
		Node getTemplate(Path file) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		String getTextFile(Path file) throws IOException {
			throw new UnsupportedOperationException();
		}
	};

}
