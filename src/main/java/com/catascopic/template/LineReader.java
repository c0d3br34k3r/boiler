package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;

public class LineReader {

	private Reader reader;
	private boolean skipLf;
	private int lineNumber;
	private int columnNumber;
	private int next;
	private char[] buf;
	private int pos;

	public boolean hasNext() {
		return next != -1;
	}

	public char next() throws IOException {
		if (pos < buf.length) {
			return buf[pos++];
		}
		char c = (char) next;
		next = read();
		return c;
	}

	private int read() throws IOException {
		int ch = reader.read();
		columnNumber++;
		if (skipLf) {
			if (ch == '\n') {
				ch = reader.read();
			}
			skipLf = false;
		}
		switch (ch) {
		case '\r':
			skipLf = true;
			// fallthrough
		case '\n':
			columnNumber = 0;
			lineNumber++;
			return '\n';
		default:
		}
		return ch;
	}

	public int lineNumber() {
		return lineNumber;
	}

	public int columnNumber() {
		return columnNumber;
	}

	public void unread(char c) throws IOException {
		if (pos == 0) {
			throw new IOException("Pushback buffer overflow");
		}
		buf[--pos] = c;
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

}
