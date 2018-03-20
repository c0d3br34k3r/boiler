package au.com.codeka.carrot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.Values;

/**
 * Scope is a collection of all the bindings that are active. The scope is
 * basically a stack: each time you iterate down a node, you can push new
 * bindings onto the stack and those will be the first one searched for a
 * variable.
 */
public class Scope implements Function<Term, Object> {

	private static final ImmutableMap<String, TemplateFunction> BUILTIN;
	static {
		Builder<String, TemplateFunction> builder = ImmutableMap.builder();
		putFunctions(Builtin.class, builder);
		BUILTIN = builder.build();
	}

	private Map<String, Object> values = new HashMap<>();
	private ImmutableMap<String, TemplateFunction> functions;

	public <F extends Enum<F> & TemplateFunction> Scope(Map<String, Object> initial) {
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

	private static <F extends Enum<F> & TemplateFunction> void putFunctions(Class<F> functions,
			ImmutableMap.Builder<String, TemplateFunction> builder) {
		for (F function : functions.getEnumConstants()) {
			builder.put(Values.separatorToCamel(function.name().toLowerCase()), function);
		}
	}

	@Nullable
	public Object resolve(@Nonnull String name) {
		return values.get(name);
	}

	public void resolve(@Nonnull String name, @Nullable Object value) {
		values.put(name, value);
	}

	public TemplateFunction getFunction(String name) {
		return functions.get(name);
	}

	@Override
	public Object apply(Term input) {
		return input.evaluate(this);
	}

}
