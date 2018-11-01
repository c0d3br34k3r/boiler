package com.catascopic.template;

@SuppressWarnings("serial")
public class TemplateParseException extends RuntimeException {

	public TemplateParseException(Locatable location, String message) {
		super(location(location) + ", " + message);
	}

	public TemplateParseException(Locatable location, Throwable e) {
		super(location(location), e);
	}

	public TemplateParseException(Locatable location, String message,
			Throwable e) {
		super(location(location) + ", " + message, e);
	}

	public TemplateParseException(Locatable location, String format,
			Object... args) {
		this(location, String.format(format, args));
	}

	public TemplateParseException(Locatable location, Throwable e,
			String format, Object... args) {
		this(location, String.format(format, args), e);
	}

	private static String location(Locatable location) {
		return String.format("at line %d, column %d",
				location.lineNumber() + 1,
				location.columnNumber() + 1);
	}

}
