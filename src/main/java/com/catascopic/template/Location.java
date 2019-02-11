package com.catascopic.template;

import java.nio.file.Path;
import java.util.Objects;

public final class Location {

	private final Path path;
	private final int line;
	private final int column;

	public Location(int line, int column) {
		this(null, line, column);
	}

	public Location(Path path, int line, int column) {
		this.path = path;
		this.line = line;
		this.column = column;
	}

	public Path path() {
		return path;
	}

	public int line() {
		return line;
	}

	public int column() {
		return column;
	}

	@Override
	public String toString() {
		return String.format("%s, line %s, column %d",
				path == null ? "[local source]" : path,
				line + 1,
				column + 1);
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
