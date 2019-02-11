package com.catascopic.template;

@SuppressWarnings("serial")
public class TemplateParseException extends RuntimeException {

	public TemplateParseException(Location location, String message) {
		super(location + ": " + message);
	}

	public TemplateParseException(Location location, Throwable e) {
		super(location.toString(), e);
	}

	public TemplateParseException(Location location, String message, Throwable e) {
		super(location + ": " + message, e);
	}

	public TemplateParseException(Location location, String format, Object... args) {
		this(location, String.format(format, args));
	}

	public TemplateParseException(Location location, Throwable e, String format, Object... args) {
		this(location, String.format(format, args), e);
	}

	public TemplateParseException(Trackable locatable, String message) {
		this(locatable.getLocation(), message);
	}

	public TemplateParseException(Trackable locatable, Throwable e) {
		this(locatable.getLocation(), e);
	}

	public TemplateParseException(Trackable locatable, String message, Throwable e) {
		this(locatable.getLocation(), message, e);
	}

	public TemplateParseException(Trackable locatable, String format, Object... args) {
		this(locatable.getLocation(), format, args);
	}

	public TemplateParseException(Trackable locatable, Throwable e, String format, Object... args) {
		this(locatable.getLocation(), e, format, args);
	}

}
