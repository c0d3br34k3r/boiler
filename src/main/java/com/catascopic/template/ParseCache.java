package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;

import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;

/**
 * Helper class used to cache parsed template files.
 */
abstract class ParseCache<T> {

	private final Map<Path, CacheEntry> cache;

	protected ParseCache() {
		this(100);
	}

	protected ParseCache(int size) {
		this.cache = new LruMap<Path, CacheEntry>(size);
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

	static class TextCache extends ParseCache<String> {

		@Override
		protected String parse(Path file) throws IOException {
			return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
		}
	}

	static class TemplateCache extends ParseCache<Node> {

		@Override
		protected Node parse(Path file) throws IOException {
			try (Reader reader = Files.newBufferedReader(file,
					StandardCharsets.UTF_8)) {
				return TemplateParser.parse(reader);
			}
		}
	}

}
