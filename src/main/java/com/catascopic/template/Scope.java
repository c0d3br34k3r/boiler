package com.catascopic.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Scope contains all necessary information for rendering a Template.
 */
public abstract class Scope implements Context, LocalAccess {

	private final LocalAccess parent;
	private Map<String, Object> locals = new HashMap<>();

	Scope(LocalAccess parent) {
		this.parent = parent;
	}

	Scope(Map<String, ?> params) {
		this(EmptyLocalAccess.EMPTY_LOCAL_ACCESS);
		locals.putAll(params);
	}

	@Override
	public final Object get(String name) {
		Object value = locals.get(name);
		if (value == null && !locals.containsKey(name)) {
			return parent.get(name);
		}
		return value;
	}

	/**
	 * Sets the value of a variable within this scope.
	 * 
	 * @param name the name of the variable
	 * @param value the value of the variable
	 */
	public final void set(String name, Object value) {
		locals.put(name, value);
	}

	/**
	 * Sets all variable names in the given map to their respective values.
	 * 
	 * @param values the map of name-value pairs
	 */
	public final void setAll(Map<String, ?> values) {
		locals.putAll(values);
	}

	/**
	 * Returns a Map containing the names and values of all variables accessible
	 * by this scope.
	 */
	public final Map<String, Object> locals() {
		Map<String, Object> collected = new HashMap<>();
		collect(collected);
		return collected;
	}

	/**
	 * Adds all variables defined by the parent scope, before adding this
	 * scope's local variables (which may overwrite the parent's).
	 */
	@Override
	public final void collect(Map<String, Object> collected) {
		parent.collect(collected);
		collected.putAll(locals);
	}

	@Override
	public final Object call(String functionName, List<Object> arguments) {
		return getFunction(functionName).apply(new Params(arguments, this));
	}

	/**
	 * Returns the TemplateFunction associated with the given name.
	 * 
	 * @throws TemplateRenderException if the referenced function does not exist
	 */
	abstract TemplateFunction getFunction(String name);

	public abstract String renderTemplate(String path, Map<String, ?> params) throws IOException;

	public abstract String renderTextFile(String path) throws IOException;

	public abstract void print(Location location, String message) throws IOException;

}
