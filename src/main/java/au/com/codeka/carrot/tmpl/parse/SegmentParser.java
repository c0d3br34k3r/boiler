package au.com.codeka.carrot.tmpl.parse;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import javax.annotation.Nullable;

import com.google.common.io.LineReader;

import au.com.codeka.carrot.CarrotException;

/**
 * The {@link SegmentParser} takes an input stream of character and turns it
 * into a stream of {@link Segment}s.
 * <p>
 * Each {@link Segment} represents a high-level component of the template. For
 * example, the following template:
 * <p>
 * <code>Text {{ hello }} stuff {% if (blah) %} more stuff {% end %}</code>
 * <p>
 * Corresponds to the following stream of segments:
 * 
 * <pre>
 * <code>
 * SegmentType=FIXED, Content="Text "
 * SegmentType=ECHO,  Content=" hello "
 * SegmentType=FIXED, Content=" stuff "
 * SegmentType=TAG,   Content=" if (blah) "
 * SegmentType=FIXED, Content=" more stuff "
 * SegmentType=TAG,   Content=" end "</code>
 * </pre>
 */
public class SegmentParser {

	private final PushbackReader reader;

	/**
	 * Construct a new {@link SegmentParser} with the given {@link LineReader},
	 * and a default {@link SegmentFactory}.
	 *
	 * @param reader
	 */
	public SegmentParser(Reader reader) {
		this.reader = new PushbackReader(reader, 1);
	}

	/**
	 * Gets the next segment from the stream, or null if there's no segments
	 * left.
	 *
	 * @return The next {@link Segment} in the stream, or null if we're at the
	 *         end of the stream.
	 * @throws CarrotException when there's an error parsing the segments.
	 */
	@Nullable
	public Segment getNext() throws IOException, CarrotException {
		Segment segment;
		do {
			segment = mode.parse(this);
		} while (segment != null && segment.getContent().isEmpty());
		return segment;
	}

	private interface ParseMode {

		Segment parse(SegmentParser parser) throws IOException, CarrotException;
	}

	private static class TagParseMode implements ParseMode {

		private final char end;
		private final SegmentType type;

		private TagParseMode(char end, SegmentType type) {
			this.end = end;
			this.type = type;
		}

		@Override
		public Segment parse(SegmentParser parser) throws IOException, CarrotException {
			return new Segment(type, parser.parseTag(end));
		}
	}

	private static final ParseMode TAG = new TagParseMode('%', SegmentType.TAG);
	private static final ParseMode ECHO = new TagParseMode('}', SegmentType.ECHO);
	private static final ParseMode COMMENT = new TagParseMode('#', SegmentType.COMMENT);

	private static final ParseMode LITERAL = new ParseMode() {

		@Override
		public Segment parse(SegmentParser parser) throws IOException {
			return new Segment(SegmentType.FIXED, parser.parseLiteral());
		}
	};

	private static final ParseMode END = new ParseMode() {

		@Override
		public Segment parse(SegmentParser parser) throws IOException {
			return null;
		}
	};

	private ParseMode mode = LITERAL;

	private String parseTag(char end) throws IOException, CarrotException {
		StringBuilder content = new StringBuilder();
		for (;;) {
			int c = reader.read();
			switch (c) {
				case -1:
					return endSegment(content, END);
				case '%':
				case '#':
				case '}':
					int c2 = reader.read();
					if (c2 == '}') {
						if (c != end) {
							throw new CarrotException("Expected " + end + "}, was " + c2 + "}");
						}
						return endSegment(content, LITERAL);
					}
					reader.unread(c2);
				default:
					content.append((char) c);
			}
		}
	}

	private String parseLiteral() throws IOException {
		StringBuilder content = new StringBuilder();
		for (;;) {
			int c = reader.read();
			switch (c) {
				case -1:
					return endSegment(content, END);
				case '{':
					int c2 = reader.read();
					switch (c2) {
						case -1:
							return endSegment(content, END);
						case '{':
							return endSegment(content, ECHO);
						case '%':
							return endSegment(content, TAG);
						case '#':
							return endSegment(content, COMMENT);
						default:
							reader.unread(c2);
					}
				default:
					content.append((char) c);
			}
		}
	}

	private String endSegment(StringBuilder content, ParseMode newMode) {
		this.mode = newMode;
		return content.toString();
	}

}
