package au.com.codeka.carrot.bindings;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.google.common.collect.UnmodifiableIterator;

import au.com.codeka.carrot.Bindings;

/**
 * {@link Bindings} based on the content of a {@link Map}.
 */
public final class MapBindings implements Bindings, Iterable<EntryBindings> {

	private final Map<String, Object> values;

	public static Builder newBuilder() {
		return new Builder();
	}

	public MapBindings(String key, Object value) {
		this(Collections.singletonMap(key, value));
	}

	public MapBindings(Map<String, Object> values) {
		this.values = values;
	}

	@Override
	public Object resolve(@Nonnull String key) {
		return values.get(key);
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public Iterator<EntryBindings> iterator() {
		final Iterator<Map.Entry<String, Object>> iterator = values.entrySet().iterator();
		return new UnmodifiableIterator<EntryBindings>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public EntryBindings next() {
				return new EntryBindings(iterator.next());
			}
		};
	}

	public static class Builder {

		private final Map<String, Object> values = new TreeMap<>();

		public Builder set(String key, Object value) {
			values.put(key, value);
			return this;
		}

		public MapBindings build() {
			return new MapBindings(values);
		}
	}

}
