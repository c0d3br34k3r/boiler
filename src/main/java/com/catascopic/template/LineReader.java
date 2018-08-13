package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;

public class LineReader {

	private Reader reader;
	private int lineNumber; // = 0
	private int columnNumber; // = 0
	private boolean skipLf;
	private char[] buf;
	private int pos;
	private int next;

	public LineReader(Reader reader, int size) throws IOException {
		this.reader = reader;
		if (size <= 0) {
			throw new IllegalArgumentException();
		}
		this.buf = new char[size];
		this.pos = size;
		next = read();
	}

	public LineReader(Reader in) throws IOException {
		this(in, 1);
	}

	public boolean hasNext() {
		return next != -1;
	}

	public char next() throws IOException {
		if (!hasNext()) {
			throw new IllegalStateException();
		}
		char c = (char) next;
		next = read();
		return c;
	}

	private int read() throws IOException {
		if (pos < buf.length) {
			return buf[pos++];
		}
		int c = reader.read();
		columnNumber = 0;
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

	public void unread(int c) throws IOException {
		if (pos == 0) {
			throw new IOException("Pushback buffer overflow");
		}
		if (hasNext()) {
			buf[--pos] = (char) next;
		}
		next = c;
	}

	public void unread(char cbuf[], int off, int len) throws IOException {
		if (len > pos) {
			throw new IOException("Pushback buffer overflow");
		}
		pos -= len;
		System.arraycopy(cbuf, off, buf, pos, len);
	}

	public void unread(char cbuf[]) throws IOException {
		unread(cbuf, 0, cbuf.length);
	}

	public int lineNumber() {
		return lineNumber;
	}

	public int columnNumber() {
		return columnNumber;
	}

}
