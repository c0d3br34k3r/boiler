package com.catascopic.template.x;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum Keyword {

	IF,
	ELSE,
	FOR,
	SET,
	TEMPLATE,
	TEXT,
	END,
	WITH,
	IN;

	private static final Map<String, Keyword> LOOKUP;

	static {
		Builder<String, Keyword> builder = ImmutableMap.builder();
		for (Keyword keyword : Keyword.values()) {
			builder.put(keyword.name().toLowerCase(), keyword);
		}
		LOOKUP = builder.build();
	}

	public static Keyword get(String token) {
		Keyword keyword = LOOKUP.get(token);
		if (keyword == null) {
			throw new IllegalArgumentException(token);
		}
		return keyword;
	}

}
