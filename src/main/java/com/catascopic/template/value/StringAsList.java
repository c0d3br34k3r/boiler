package com.catascopic.template.value;

import java.util.AbstractList;
import java.util.RandomAccess;

class StringAsList extends AbstractList<String> implements RandomAccess {

	private final String str;

	StringAsList(String str) {
		this.str = str;
	}

	@Override
	public String get(int index) {
		return String.valueOf(str.charAt(index));
	}

	@Override
	public int size() {
		return str.length();
	}
}
