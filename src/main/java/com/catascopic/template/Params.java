package com.catascopic.template;

import java.util.List;

public class Params {

	private List<Object> list;

	public Params(List<Object> list) {
		this.list = list;
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		if (list.size() != 1) {
			throw new TemplateEvalException(
					"expected 1 param, got %d", list.size());
		}
		return (T) list.get(0);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(int i) {
		return (T) list.get(i);
	}

	@SuppressWarnings("unchecked")
	public <T> T getOrDefault(int index, T defaultValue) {
		return index < list.size() ? (T) list.get(index) : defaultValue;
	}

	public List<Object> asList() {
		return list;
	}

	public int size() {
		return list.size();
	}

}
