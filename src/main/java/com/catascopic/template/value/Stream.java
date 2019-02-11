package com.catascopic.template.value;

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

			int i; // = 0

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public List<Object> next() {
				Object value = iterator.next();
				return Arrays.asList(i++, !iterator.hasNext(), value);
			}
		};
	}

	@Override
	public String toString() {
		return "stream of " + items;
	}

}
