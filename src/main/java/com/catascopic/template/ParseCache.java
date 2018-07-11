package com.catascopic.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.catascopic.template.parse.ContentNode;
import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.Parser;
import com.google.common.cache.Cache;
import com.google.common.io.CharStreams;

/**
 * Helper class used to cache parsed template files.
 */
public class ParseCache {

	private static final long DEFAULT_SIZE = 256;

	private final Cache<Path, CacheEntry> cache;

	public Node getDocument(final Path path, final boolean template)
			throws ExecutionException, IOException {
		if (!Files.isRegularFile(path)) {
			cache.invalidate(path);
		}
		CacheEntry entry = cache.get(path, new Callable<CacheEntry>() {

			@Override
			public CacheEntry call() throws Exception {
				return readDocument(path, template);
			}
		});
		if (Files.getLastModifiedTime(path).compareTo(entry.modified) > 0) {
			entry = readDocument(path, template);
			cache.put(path, entry);
		}
		return entry.node;
	}

	private static CacheEntry readDocument(Path path, boolean template)
			throws IOException {
		try (BufferedReader in = Files.newBufferedReader(path,
				StandardCharsets.UTF_8)) {
			Node document;
			if (template) {
				document = Parser.parse(in);
			} else {
				document = new ContentNode(CharStreams.toString(in));
			}
			return new CacheEntry(document,
					Files.getLastModifiedTime(path));
		}
	}

	private static class CacheEntry {

		Node node;
		FileTime modified;

		CacheEntry(Node node, FileTime modifiedTime) {
			this.node = node;
			this.modified = modifiedTime;
		}
	}
}
