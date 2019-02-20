package com.catascopic.template.value;

import java.util.AbstractList;
import java.util.RandomAccess;

class Range extends AbstractList<Integer> implements RandomAccess {

	private final int offset;
	private final int size;
	private final int step;

	Range(int offset, int size, int step) {
		this.offset = offset;
		this.size = size;
		this.step = step;
	}

	@Override
	public Integer get(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}
		return offset + step * index;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return String.format("range(%s, %s, %s)", offset, offset + size * step, step);
	}

}
