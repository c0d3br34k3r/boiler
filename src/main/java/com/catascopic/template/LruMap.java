package com.catascopic.template;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class LruMap<K, V> extends LinkedHashMap<K, V> {

	private final int maxEntries;

	public LruMap(int maxEntries) {
		this(maxEntries, 16);
	}

	public LruMap(int maxEntries, int initialCapacity) {
		this(maxEntries, initialCapacity, 0.75f);
	}

	public LruMap(int maxEntries, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, true);
		this.maxEntries = maxEntries;
	}

	@Override
	protected final boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxEntries;
	}

}
