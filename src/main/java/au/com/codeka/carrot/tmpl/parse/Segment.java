package au.com.codeka.carrot.tmpl.parse;

import java.util.Objects;

/**
 * Represents a segment in a stream of segments from the {@link SegmentParser}.
 */
public class Segment {

	private final SegmentType type;
	private final String value;

	/**
	 * Create a new {@link Segment}.
	 *
	 * @param type The {@link SegmentType} of the segment to create.
	 * @param content The content to include in the segment.
	 * @return A new {@link Segment}.
	 */
	public Segment(SegmentType type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * @return The {@link SegmentType} of this segment.
	 */
	public SegmentType getType() {
		return type;
	}

	/**
	 * @return The contents of this segment.
	 */
	public String getContent() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Segment)) {
			return false;
		}
		Segment other = ((Segment) obj);
		return other.type == type && other.value.equals(value);
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
