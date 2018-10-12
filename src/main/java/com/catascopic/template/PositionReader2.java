package com.catascopic.template;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class PositionReader2 {

	private Reader reader;
	private int lineNumber; // = 0
	private int columnNumber; // = 0
	private boolean skipLf;
	private int peeked;

	public PositionReader2(Reader reader) {
		this.reader = reader;
	}
	
	public boolean hasNext() {
		return getPeek() != -1;
	}

	private int getPeek() {
		// TODO Auto-generated method stub
		return 0;
	}

	public char peek() {
		// TODO Auto-generated method stub
		return 0;
	}

	public char next() throws IOException {
		if (peeked == -1) {
			int ch = read();
			if (ch == -1) {
				throw new EOFException();
			}
			return (char) ch;
		}
		return (char) peeked;
	}

	private int read() throws IOException {
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
