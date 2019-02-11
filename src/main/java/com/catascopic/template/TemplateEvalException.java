package com.catascopic.template;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TemplateEvalException extends RuntimeException {

	private List<Location> trace = new ArrayList<>();

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

	public TemplateEvalException(Throwable cause, String format, Object... args) {
		super(String.format(format, args), cause);
	}

	public void addLocation(Location location) {
		trace.add(location);
	}

	public List<Location> getTrace() {
		return trace;
	}

}
