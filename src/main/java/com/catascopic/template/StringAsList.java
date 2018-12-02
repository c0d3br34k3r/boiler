package com.catascopic.template;

import java.util.AbstractList;
import java.util.RandomAccess;

class StringAsList extends AbstractList<String>
		implements RandomAccess {

	private final String string;

	StringAsList(String str) {
		this.string = str;
	}

	@Override
	public String get(int index) {
		return String.valueOf(string.charAt(index));
	}

	@Override
	public int size() {
		return string.length();
	}
}
