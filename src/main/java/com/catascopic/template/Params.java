package com.catascopic.template;

import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;

public class Params {

	private final List<Object> list;
	private final Scope scope;

	public Params(List<Object> list, Scope scope) {
		this.list = list;
		this.scope = scope;
	}

	public boolean getBoolean(int index) {
		return Values.isTrue(get(index));
	}

	public boolean getBoolean(int index, boolean defaultValue) {
		return index < list.size() ? getBoolean(index) : defaultValue;
	}

	public int getInt(int index) {
		return Values.toNumber(get(index)).intValue();
	}

	public int getInt(int index, int defaultValue) {
		return index < list.size() ? getInt(index) : defaultValue;
	}

	public double getDouble(int index) {
		return Values.toNumber(get(index)).doubleValue();
	}

	public double getDouble(int index, double defaultValue) {
		return index < list.size() ? getDouble(index) : defaultValue;
	}

	public Number getNumber(int index) {
		return Values.toNumber(get(index));
	}

	public Number getNumber(int index, Number defaultValue) {
		return index < list.size() ? getNumber(index) : defaultValue;
	}

	public String getString(int index) {
		return Values.toString(get(index));
	}

	public String getString(int index, String defaultValue) {
		return index < list.size() ? getString(index) : defaultValue;
	}

	public Iterable<?> getIterable(int index) {
		return Values.toIterable(get(index));
	}

	public Iterable<?> getIterable(int index, Iterable<?> defaultValue) {
		return index < list.size() ? getIterable(index) : defaultValue;
	}

	@SuppressWarnings("unchecked")
	public Map<String, ?> getMap(int index) {
		return (Map<String, ?>) get(index);
	}

	public Map<String, ?> getMap(int index, Map<String, ?> defaultValue) {
		return index < list.size() ? getMap(index) : defaultValue;
	}

	public Object get(int index) {
		if (list.size() <= index) {
			throw new TemplateEvalException("missing parameter %s in %s ",
					index, this);
		}
		return list.get(index);
	}

	public Object get(int index, Object defaultValue) {
		return index < list.size() ? list.get(index) : defaultValue;
	}

	public List<Object> asList() {
		return list;
	}

	public int size() {
		return list.size();
	}

	public Scope scope() {
		return scope;
	}

	@Override
	public String toString() {
		return Joiner.on(", ").join(list);
	}

	public char getChar(int index, String defaultValue) {
		String str = getString(index, defaultValue);
		if (str.length() != 1) {
			throw new TemplateEvalException(
					"param %d must be a 1-char string, but was %s", index, str);
		}
		return str.charAt(0);
	}

}
