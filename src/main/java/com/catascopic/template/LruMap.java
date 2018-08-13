package com.catascopic.template;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class LruMap<K, V> extends LinkedHashMap<K, V> {

	private final int maxEntries;

	public LruMap(int maxEntries) {
		super(16, 0.75f, true);
		this.maxEntries = maxEntries;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxEntries;
	}

}
