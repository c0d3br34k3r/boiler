package com.catascopic.template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Debuggers {
	private Debuggers() {}

	public static final Debugger STANDARD_OUTPUT = new Debugger() {

		@Override
		public void print(Location location, String message) {
			System.out.println(location + ": " + message);
		}
	};

	public static Debugger appendTo(final Appendable appendable) {
		return new Debugger() {

			@Override
			public void print(Location location, String message) throws IOException {
				appendable.append(location.toString()).append(": ").append(message).append('\n');
			}
		};
	}

	public static Debugger appendTo(final Path file) {
		return new Debugger() {

			@Override
			public void print(Location location, String message) throws IOException {
				Files.write(file, (location + ": " + message + "\n")
						.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			}
		};
	}

}
