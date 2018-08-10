package com.catascopic.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.catascopic.template.parse.ContentNode;
import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.Parser;
import com.google.common.io.CharStreams;

/**
 * Helper class used to cache parsed template files.
 */
public class ParseCache {

	private final Map<List<Object>, CacheEntry> cache;

	public ParseCache() {
		this(new LruMap<List<Object>, CacheEntry>(99));
	}

	public ParseCache(Map<List<Object>, CacheEntry> map) {
		this.cache = map;
	}

	public Node getDocument(Path path, boolean template) throws IOException {
		// TODO: make dedicated key object?
		List<Object> key = Arrays.<Object> asList(path, template);
		CacheEntry entry;
		synchronized (cache) {
			entry = cache.get(key);
			if (entry == null) {
				entry = new CacheEntry(path, template);
				cache.put(key, entry);
			} else {
				entry.refresh(path, template);
			}
		}
		return entry.node;
	}

	private static Node parse(Path path, boolean template) throws IOException {
		try (BufferedReader in = Files.newBufferedReader(path,
				StandardCharsets.UTF_8)) {
			return template
					? Parser.parse(in)
					: new ContentNode(CharStreams.toString(in));
		}
	}

	private static class CacheEntry {

		Node node;
		FileTime modified;

		CacheEntry(Path path, boolean template) throws IOException {
			node = parse(path, template);
			modified = Files.getLastModifiedTime(path);
		}

		void refresh(Path path, boolean template) throws IOException {
			FileTime fileTime = Files.getLastModifiedTime(path);
			if (!fileTime.equals(modified)) {
				node = parse(path, template);
				modified = fileTime;
			}
		}
	}

}
