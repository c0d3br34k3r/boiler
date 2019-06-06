package com.catascopic.template;

import java.util.Map;

public enum EmptyLocalAccess implements LocalAccess {
	EMPTY;

	@Override
	public Object get(String name) {
		throw new TemplateRenderException("%s is undefined", name);
	}

	@Override
	public void collect(Map<String, Object> locals) {
		// nothing to add
	}

}
