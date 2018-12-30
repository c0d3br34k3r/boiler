package com.catascopic.template;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Rendering {

	public static String render(String text, Map<String, Object> params) {
		return Template.parse(text).render(params);
	}

	public static String render(String text) {
		return render(text, ImmutableMap.<String, Object>of());
	}

	public static String render(String text, String key1, Object value1) {
		return render(text, ImmutableMap.of(key1, value1));
	}

	public static String render(String text, String key1, Object value1,
			String key2, Object value2) {
		return render(text, ImmutableMap.of(key1, value1, key2, value2));
	}

	public static String render(String text, String key1, Object value1,
			String key2, Object value2,
			String key3, Object value3) {
		return render(text, ImmutableMap.of(key1, value1,
				key2, value2,
				key3, value3));
	}

	public static String render(String text, String key1, Object value1,
			String key2, Object value2,
			String key3, Object value3,
			String key4, Object value4) {
		return render(text, ImmutableMap.of(key1, value1,
				key2, value2,
				key3, value3,
				key4, value4));
	}

	public static String render(String text, String key1, Object value1,
			String key2, Object value2,
			String key3, Object value3,
			String key4, Object value4,
			String key5, Object value5) {
		return render(text, ImmutableMap.of(key1, value1,
				key2, value2,
				key3, value3,
				key4, value4,
				key5, value5));
	}

}
