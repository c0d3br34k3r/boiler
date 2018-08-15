package com.catascopic.template;

import java.io.IOException;
import java.io.Reader;

public class PositionReader {

	private Reader reader;
	private int lineNumber; // = 0
	private int columnNumber; // = 0
	private boolean skipLf;
	private char[] buf;
	private int pos;

	public PositionReader(Reader reader, int size) {
		this.reader = reader;
		if (size <= 0) {
			throw new IllegalArgumentException();
		}
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

	public TemplateParseException parseError(String message, Throwable cause) {
		return new TemplateParseException(lineNumber, columnNumber, message,
				cause);
	}

	public TemplateParseException parseError(String message) {
		return new TemplateParseException(lineNumber, columnNumber, message);
	}

	public TemplateParseException parseError(Throwable cause) {
		return new TemplateParseException(lineNumber, columnNumber, cause);
	}

	public TemplateParseException parseError(String format, Object... args) {
		return new TemplateParseException(lineNumber, columnNumber, format,
				args);
	}

	public TemplateParseException parseError(Throwable cause, String format,
			Object... args) {
		return new TemplateParseException(lineNumber, columnNumber, cause,
				format,
				args);
	}

}
