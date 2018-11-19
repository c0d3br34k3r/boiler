package com.catascopic.template;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.UnmodifiableIterator;

class Enumeration implements Iterable<List<Object>> {

	private final Iterable<?> items;

	Enumeration(Iterable<?> items) {
		this.items = items;
	}

	@Override
	public Iterator<List<Object>> iterator() {
		final Iterator<?> iterator = items.iterator();
		return new UnmodifiableIterator<List<Object>>() {

			int i; // = 0

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public List<Object> next() {
				return Arrays.asList(i++, iterator.next());
			}
		};
	}

	@Override
	public String toString() {
		return "enumeration of " + items;
	}

}
