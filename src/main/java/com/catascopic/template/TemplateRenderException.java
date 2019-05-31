package com.catascopic.template;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TemplateRenderException extends RuntimeException {

	private List<Location> trace = new ArrayList<>();

	public TemplateRenderException(Throwable cause, String message) {
		super(message, cause);
	}

	public TemplateRenderException(String message) {
		super(message);
	}

	public TemplateRenderException(String format, Object... args) {
		super(String.format(format, args));
	}

	public TemplateRenderException(Throwable cause) {
		super(cause);
	}

	public TemplateRenderException(Throwable cause, String format, Object... args) {
		super(String.format(format, args), cause);
	}

	public TemplateRenderException addLocation(Location location) {
		trace.add(location);
		return this;
	}

	public List<Location> getTrace() {
		return trace;
	}

}
