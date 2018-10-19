package com.catascopic.template;

public enum Null {

	NULL;

	public static <T> T orDefault(T nullable, T defaultValue) {
		return nullable == NULL ? defaultValue : nullable;
	}

}
