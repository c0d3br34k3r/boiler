package au.com.codeka.carrot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import au.com.codeka.carrot.resource.ResourceName;
import au.com.codeka.carrot.tmpl.Node;

/**
 * Helper class used to cache parsed template files.
 */
public class ParseCache {

	private static final long DEFAULT_SIZE = 256;
	private final Configuration config;
	private final Cache<ResourceName, CacheEntry> cache;

	public ParseCache(Configuration config) {
		this.config = config;
		cache = CacheBuilder.newBuilder().maximumSize(DEFAULT_SIZE).build();
	}

	public Node getNode(ResourceName resourceName) throws CarrotException {
		CacheEntry entry = cache.getIfPresent(resourceName);
		if (entry != null) {
			long modifiedTime = config.getResourceLocator().getModifiedTime(resourceName);
			if (modifiedTime != entry.modifiedTime) {
				cache.invalidate(resourceName);
				return null;
			}
			return entry.node;
		}
		return null;
	}

	public void addNode(ResourceName resourceName, Node node) throws CarrotException {
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
