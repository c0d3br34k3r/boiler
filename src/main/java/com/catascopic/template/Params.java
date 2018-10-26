package com.catascopic.template;

import java.util.List;
import java.util.Map;

public class Params {

	private List<Object> list;

	public Params(List<Object> list) {
		this.list = list;
	}

	public boolean getBoolean(int index) {
		return Values.isTrue(get(index));
	}

	public boolean getInt(int index, boolean defaultValue) {
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

	public Map<?, ?> getMap(int index) {
		return (Map<?, ?>) get(index);
	}

	public Map<?, ?> getMap(int index, Map<?, ?> defaultValue) {
		return index < list.size() ? getMap(index) : defaultValue;
	}

	public Object get(int index) {
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

}
