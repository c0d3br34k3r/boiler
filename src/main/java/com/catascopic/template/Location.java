package com.catascopic.template;

import java.util.Objects;

public final class Location {

	private final int line;
	private final int column;

	public Location(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}

	@Override
	public String toString() {
		return String.format("line %s, column %d", line + 1, column + 1);
	}

	@Override
	public int hashCode() {
		return Objects.hash(line, column);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Location)) {
			return false;
		}
		Location that = (Location) obj;
		return this.line == that.line && this.column == that.column;
	}

}
