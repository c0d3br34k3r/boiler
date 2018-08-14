package com.catascopic.template;

import java.util.Map;

public class TestUtil {

	public static Scope testScope(Map<String, Object> params) {
		return new Scope(TemplateResolver.DEFAULT, null, params);
	}
	
}
