package com.catascopic.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.catascopic.template.expr.Values;
import com.google.common.collect.UnmodifiableIterator;

class Zip implements Iterable<List<?>> {

	private final Iterable<?> iterables;

	Zip(Iterable<?> iterables) {
		this.iterables = iterables;
	}

	@Override
	public Iterator<List<?>> iterator() {
		final List<Iterator<?>> iterators = new ArrayList<>();
		for (Object value : iterables) {
			iterators.add(Values.toIterable(value).iterator());
		}
		return new UnmodifiableIterator<List<?>>() {

			@Override
			public boolean hasNext() {
				for (Iterator<?> iterator : iterators) {
					if (!iterator.hasNext()) {
						return false;
					}
				}
				return true;
			}

			@Override
			public List<?> next() {
				List<Object> values = new ArrayList<>(iterators.size());
				for (Iterator<?> iterator : iterators) {
					values.add(iterator.next());
				}
				return values;
			}
		};
	}

}
