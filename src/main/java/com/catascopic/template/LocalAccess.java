package com.catascopic.template;

import java.util.Map;

interface LocalAccess {

	Object get(String name);

	void collect(Map<String, Object> locals);

}
