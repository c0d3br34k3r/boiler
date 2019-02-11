package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class TrackingReader implements Trackable {

	private Reader reader;
	private int line; // = 0
	private int column; // = 0
	private boolean skipLf;
	private char[] buf;
	private int pos;

	private Path path;

	public static TrackingReader create(Reader reader) {
		return new TrackingReader(reader, null);
	}

	public static TrackingReader create(Reader reader, Path path) {
		return new TrackingReader(reader, path);
	}

	private TrackingReader(Reader reader, int size, Path path) {
		this.reader = reader;
		this.buf = new char[size];
		this.pos = size;
		this.path = path;
	}

	private TrackingReader(Reader reader, Path path) {
		this(reader, 1, path);
	}

	public int read() throws IOException {
		if (pos < buf.length) {
			return buf[pos++];
		}
		int c = reader.read();
		if (c != -1) {
			column++;
		}
		if (skipLf) {
			if (c == '\n') {
				c = reader.read();
			}
			skipLf = false;
		}
		switch (c) {
		case '\r':
			skipLf = true;
			// fallthrough
		case '\n':
			line++;
			column = 0;
			return '\n';
		}
		return c;
	}

	public void unread(int c) {
		if (pos == 0) {
			throw new IllegalStateException();
		}
		buf[--pos] = (char) c;
	}

	public boolean tryRead(char match) throws IOException {
		int ch = read();
		if (ch == match) {
			return true;
		}
		if (ch != -1) {
			unread(ch);
		}
		return false;
	}

	@Override
	public Location getLocation() {
		return new Location(path, line, column);
	}

}
