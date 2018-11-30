package com.catascopic.template;

import java.io.IOException;
import java.util.Map;

public abstract class Template {

	public abstract void render(Appendable writer, Map<String, Object> params)
			throws IOException;

	public String render(Map<String, Object> params) {
		StringBuilder appendable = new StringBuilder();
		try {
			render(appendable, params);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return appendable.toString();
	}

}
