package au.com.codeka.carrot.bindings;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.UnmodifiableIterator;

import au.com.codeka.carrot.Bindings;

/**
 * {@link Bindings} based on the content of a {@link Map}.
 */
public final class MapBindings implements Bindings, Iterable<EntryBindings> {

	private final Map<String, Object> values;

	public MapBindings(String key, Object value) {
		this(Collections.singletonMap(key, value));
	}

	public MapBindings(Map<String, Object> values) {
		this.values = values;
	}

	@Override
	public Object resolve(String key) {
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

}
