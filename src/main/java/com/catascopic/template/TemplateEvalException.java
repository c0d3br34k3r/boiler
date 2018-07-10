package com.catascopic.template;

public class TemplateEvalException extends RuntimeException {

	public TemplateEvalException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateEvalException(String message) {
		super(message);
	}

	public TemplateEvalException(String format, Object... args) {
		super(String.format(format, args));
	}

	public TemplateEvalException(Throwable cause) {
		super(cause);
	}

}
