package com.catascopic.template;

public class TemplateParseException extends RuntimeException {

	public TemplateParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateParseException(String message) {
		super(message);
	}

	public TemplateParseException(String format, Object... args) {
		super(String.format(format, args));
	}

	public TemplateParseException(Throwable cause) {
		super(cause);
	}

}
