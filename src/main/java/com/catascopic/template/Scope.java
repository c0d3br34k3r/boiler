package com.catascopic.template;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Scope implements Function<Term, Object> {

	private static final ImmutableMap<String, TemplateFunction> BUILTIN;
	static {
		Builder<String, TemplateFunction> builder = ImmutableMap.builder();
		putFunctions(Builtin.class, builder);
		BUILTIN = builder.build();
	}

	private Map<String, Object> values = new HashMap<>();
	private ImmutableMap<String, TemplateFunction> functions;

	public <F extends Enum<F> & TemplateFunction> Scope(
			Map<String, Object> initial) {
		this(initial, Collections.<Class<F>> emptyList());
	}

	public <F extends Enum<F> & TemplateFunction> Scope(
			Map<String, Object> initial,
			Collection<Class<F>> functionClasses) {
		values.putAll(initial);
		Builder<String, TemplateFunction> builder = ImmutableMap.builder();
		builder.putAll(BUILTIN);
		for (Class<F> clazz : functionClasses) {
			putFunctions(clazz, builder);
		}
		functions = builder.build();
	}

	private static <F extends Enum<F> & TemplateFunction> void putFunctions(
			Class<F> functions,
			ImmutableMap.Builder<String, TemplateFunction> builder) {
		for (F function : functions.getEnumConstants()) {
			builder.put(Values.separatorToCamel(function.name().toLowerCase()),
					function);
		}
	}

	@Nullable
	public Object resolve(@Nonnull String name) {
		Object value = values.get(name);
		if (value == null) {
			throw new TemplateParseException("%s is undefined", name);
		}
		return value;
	}

	public void set(@Nonnull String name, @Nullable Object value) {
		values.put(name, value);
	}

	public TemplateFunction getFunction(String name) {
		TemplateFunction func = functions.get(name);
		if (func == null) {
			throw new TemplateParseException("function %s is undefined", name);
		}
		return func;
	}

	@Override
	public Object apply(Term input) {
		return input.evaluate(this);
	}

	public void renderTemplate(Appendable writer, String string) {
		// TODO Auto-generated method stub

	}

	public void renderTextFile(Appendable writer, String string) {
		// TODO Auto-generated method stub

	}

}
