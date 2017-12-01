package au.com.codeka.carrot.tmpl.parse;

import java.util.Objects;

/**
 * Represents a token in a stream of tokens from the {@link ContentParser}.
 */
public class Content {

	private final ContentType type;
	private final String value;

	/**
	 * Create a new {@link Content}.
	 *
	 * @param type The {@link ContentType} of the token to create.
	 * @param content The content to include in the token.
	 * @return A new {@link Content}.
	 */
	public Content(ContentType type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * @return The {@link ContentType} of this token.
	 */
	public ContentType getType() {
		return type;
	}

	/**
	 * @return The contents of this token.
	 */
	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Content) {
			return ((Content) other).type == type
					&& ((Content) other).value.equals(value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public String toString() {
		return String.format("%s <%s>", type, value);
	}

}
