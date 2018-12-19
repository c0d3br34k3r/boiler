package com.catascopic.template;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.UnmodifiableIterator;

class Stream implements Iterable<List<Object>> {

	private final Iterable<?> items;

	Stream(Iterable<?> items) {
		this.items = items;
	}

	@Override
	public Iterator<List<Object>> iterator() {
		final Iterator<?> iterator = items.iterator();
		return new UnmodifiableIterator<List<Object>>() {

			boolean first = true;

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public List<Object> next() {
				boolean wasFirst = first;
				first = false;
				return Arrays.asList(iterator.next(),
						wasFirst, !iterator.hasNext());
			}
		};
	}

	@Override
	public String toString() {
		return "stream of " + items;
	}

}
