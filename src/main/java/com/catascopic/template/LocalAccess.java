package com.catascopic.template;

import java.util.Map;

interface LocalAccess {

	Object get(String name);

	void collectLocals(Map<String, Object> collected);

}
