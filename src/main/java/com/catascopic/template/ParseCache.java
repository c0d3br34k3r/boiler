package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;

abstract class ParseCache<T> {

	private Map<Path, CacheEntry> cache;

	protected ParseCache(int size) {
		this.cache = new LruMap<>(size);
	}

	T get(Path file) throws IOException {
		CacheEntry entry;
		synchronized (cache) {
			entry = cache.get(file);
			if (entry == null) {
				entry = new CacheEntry(file);
				cache.put(file, entry);
			} else {
				entry.refresh(file);
			}
		}
		return entry.parsed;
	}

	protected abstract T parse(Path file) throws IOException;

	private class CacheEntry {

		T parsed;
		FileTime modified;

		CacheEntry(Path file) throws IOException {
			parsed = parse(file);
			modified = Files.getLastModifiedTime(file);
		}

		void refresh(Path file) throws IOException {
			FileTime fileTime = Files.getLastModifiedTime(file);
			if (!fileTime.equals(modified)) {
				parsed = parse(file);
				modified = fileTime;
			}
		}
	}

}
