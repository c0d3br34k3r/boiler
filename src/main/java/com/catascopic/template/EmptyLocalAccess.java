package com.catascopic.template;

import java.util.Map;

/**
 * Implementation of LocalAccess where no variables exist.
 */
public enum EmptyLocalAccess implements LocalAccess {

	/**
	 * The singleton {@link EmptyLocalAccess}.
	 */
	EMPTY_LOCAL_ACCESS;

	@Override
	public Object get(String name) {
		throw new TemplateRenderException("%s is undefined", name);
	}

	@Override
	public void collect(Map<String, Object> locals) {
		// nothing to add
	}

}
