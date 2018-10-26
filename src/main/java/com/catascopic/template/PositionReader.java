package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;

public class PositionReader implements Locatable {

	private Reader reader;
	private int lineNumber; // = 0
	private int columnNumber; // = 0
	private boolean skipLf;
	private char[] buf;
	private int pos;

	public PositionReader(Reader reader, int size) {
		this.reader = reader;
		this.buf = new char[size];
		this.pos = size;
	}

	public PositionReader(Reader in) {
		this(in, 1);
	}

	public int read() throws IOException {
		if (pos < buf.length) {
			return buf[pos++];
		}
		int c = reader.read();
		if (c != -1) {
			columnNumber++;
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
			lineNumber++;
			columnNumber = 0;
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

	public int lineNumber() {
		return lineNumber;
	}

	public int columnNumber() {
		return columnNumber;
	}

}
