package au.com.codeka.carrot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import au.com.codeka.carrot.tmpl.Node;

/**
 * Helper class used to cache parsed template files.
 */
public class ParseCache {

	private static final long DEFAULT_SIZE = 256;
	private final Configuration config;
	private final Cache<Path, CacheEntry> cache;

	public ParseCache(Configuration config) {
		this.config = config;
		cache = CacheBuilder.newBuilder().maximumSize(DEFAULT_SIZE).build();
	}

	public Node getNode(Path path) throws CarrotException {
		CacheEntry entry = cache.getIfPresent(path);
		if (entry != null) {
			long modifiedTime;
			try {
				modifiedTime = Files.getLastModifiedTime(path).toMillis();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (modifiedTime != entry.modifiedTime) {
				cache.invalidate(path);
				return null;
			}
			return entry.node;
		}
		return null;
	}

	public void addNode(Path resourceName, Node node) throws CarrotException {
		long modifiedTime = config.getResourceLocator().getModifiedTime(resourceName);
		cache.put(resourceName, new CacheEntry(node, modifiedTime));
	}

	private static class CacheEntry {
		Node node;
		long modifiedTime;

		public CacheEntry(Node node, long modifiedTime) {
			this.node = node;
			this.modifiedTime = modifiedTime;
		}
	}
}
