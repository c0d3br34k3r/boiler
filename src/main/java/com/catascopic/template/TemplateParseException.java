package com.catascopic.template;

@SuppressWarnings("serial")
public class TemplateParseException extends RuntimeException {

	private final int lineNumber;
	private final int columnNumber;

	TemplateParseException(int lineNumber, int columnNumber, String message) {
		super("[" + location(lineNumber, columnNumber) + "] " + message);
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	TemplateParseException(int lineNumber, int columnNumber, Throwable e) {
		super(location(lineNumber, columnNumber), e);
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	TemplateParseException(int lineNumber, int columnNumber, String message,
			Throwable e) {
		super("[" + location(lineNumber, columnNumber) + "] " + message, e);
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	TemplateParseException(int lineNumber, int columnNumber, String format,
			Object... args) {
		this(lineNumber, columnNumber, String.format(format, args));
	}

	TemplateParseException(int lineNumber, int columnNumber, Throwable e,
			String format, Object... args) {
		this(lineNumber, columnNumber, String.format(format, args), e);
	}

	private static String location(int lineNumber, int columnNumber) {
		return String.format("line %d, column %d", lineNumber + 1,
				columnNumber);
	}

	public int lineNumber() {
		return lineNumber;
	}

	public int columnNumber() {
		return columnNumber;
	}

}
