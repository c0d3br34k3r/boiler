package com.catascopic.template;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import com.catascopic.template.expr.Values;
import com.catascopic.template.parse.Node;
import com.catascopic.template.value.Value;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class TemplateEngine {

	private static final ImmutableMap<String, TemplateFunction> BUILTIN;
	static {
		Builder<String, TemplateFunction> builder = ImmutableMap.builder();
		putFunctions(Builtin.class, builder);
		BUILTIN = builder.build();
	}

	private final ImmutableMap<String, TemplateFunction> functions;

	private static <F extends Enum<F> & TemplateFunction> ImmutableMap<String, TemplateFunction> buildFunctionMap(
			Collection<Class<F>> functionClasses) {
		Builder<String, TemplateFunction> builder = ImmutableMap.builder();
		builder.putAll(BUILTIN);
		for (Class<F> clazz : functionClasses) {
			putFunctions(clazz, builder);
		}
		return builder.build();
	}

	private static <F extends Enum<F> & TemplateFunction> void putFunctions(
			Class<F> functions,
			ImmutableMap.Builder<String, TemplateFunction> builder) {
		for (F function : functions.getEnumConstants()) {
			builder.put(Values.separatorToCamel(function.name().toLowerCase()),
					function);
		}
	}

	public void render(Path file,
			Appendable writer,
			Map<String, Value> parameters) {

		Node template;
		Scope.create(this, file.getParent());

		template.render(writer, scope);
	}

	TemplateFunction getFunction(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	Node getTemplate(String fileName) {
		// TODO Auto-generated method stub

	}

	Node getTextFile(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
