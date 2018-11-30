package com.catascopic.template;

import java.util.Map;

abstract class LocalAccess {

	public abstract Object get(String name);

	protected abstract void collect(Map<String, Object> locals);

}
