package com.catascopic.template;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class LruMap<K, V> extends LinkedHashMap<K, V> {

	private final int maxEntries;

	public LruMap(int maxEntries) {
		this(maxEntries, 0.75f);
	}

	public LruMap(int maxEntries, float loadFactor) {
		super(maxEntries + 1, loadFactor, true);
		this.maxEntries = maxEntries;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxEntries;
	}

}
