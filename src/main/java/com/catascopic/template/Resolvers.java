package com.catascopic.template;

import java.util.Map;

public class Resolvers {

	private static final Resolver EMPTY = new Resolver() {

		@Override
		public Object get(String name) {
			throw new TemplateEvalException(name);
		}
	};

	public static Resolver fromMap(final Map<String, Object> map) {
		return new Resolver() {

			@Override
			public Object get(String name) {
				Object value = map.get(name);
				if (value == null) {
					throw new TemplateEvalException(name);
				}
				return value;
			}
		};
	}

	public static Resolver empty() {
		return EMPTY;
	}

}
