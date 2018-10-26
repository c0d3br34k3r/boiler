package com.catascopic.template;

@SuppressWarnings("serial")
public class TemplateParseException extends RuntimeException {

	private final int lineNumber;
	private final int columnNumber;

	public TemplateParseException(Locatable reader, String message) {
		super("[" + location(reader) + "] " + message);
		this.lineNumber = reader.lineNumber();
		this.columnNumber = reader.columnNumber();
	}

	public TemplateParseException(Locatable reader, Throwable e) {
		super(location(reader), e);
		this.lineNumber = reader.lineNumber();
		this.columnNumber = reader.columnNumber();
	}

	public TemplateParseException(Locatable reader, String message,
			Throwable e) {
		super("[" + location(reader) + "] " + message, e);
		this.lineNumber = reader.lineNumber();
		this.columnNumber = reader.columnNumber();
	}

	public TemplateParseException(Locatable reader, String format,
			Object... args) {
		this(reader, String.format(format, args));
	}

	public TemplateParseException(Locatable reader, Throwable e,
			String format, Object... args) {
		this(reader, String.format(format, args), e);
	}

	private static String location(Locatable reader) {
		return String.format("line %d, column %d",
				reader.lineNumber() + 1,
				reader.columnNumber() + 1);
	}

	public int lineNumber() {
		return lineNumber;
	}

	public int columnNumber() {
		return columnNumber;
	}

}
