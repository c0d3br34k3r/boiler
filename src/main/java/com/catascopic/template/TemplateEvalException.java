package com.catascopic.template;

@SuppressWarnings("serial")
public class TemplateEvalException extends RuntimeException {

	public TemplateEvalException(Throwable cause, String message) {
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

	public TemplateEvalException(Location location, Throwable cause,
			String message) {
		super("at " + location + " " + message, cause);
	}

	public TemplateEvalException(Location location, String message) {
		super("at " + location + " " + message);
	}

	public TemplateEvalException(Location location, String format,
			Object... args) {
		super("at " + location + " " + String.format(format, args));
	}

	public TemplateEvalException(Location location, Throwable cause) {
		super("at " + location, cause);
	}

}
