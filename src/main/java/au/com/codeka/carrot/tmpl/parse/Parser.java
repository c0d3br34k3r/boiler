package au.com.codeka.carrot.tmpl.parse;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import javax.annotation.Nullable;
import javax.swing.text.Segment;

import com.google.common.io.LineReader;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.TagType;
import au.com.codeka.carrot.tmpl.FixedNode;
import au.com.codeka.carrot.tmpl.Node;

/**
 * The {@link Parser} takes an input stream of character and turns it into a
 * stream of {@link Segment}s.
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
public class Parser {

	private final PushbackReader reader;

	/**
	 * Construct a new {@link Parser} with the given {@link LineReader}, and a
	 * default {@link SegmentFactory}.
	 *
	 * @param reader
	 */
	public Parser(Reader reader) {
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
	public Node getNext() throws IOException, CarrotException {
		return mode.parse(this);
	}

	private interface ParseMode {

		Node parse(Parser parser) throws IOException, CarrotException;
	}

	private static final ParseMode TAG = new ParseMode() {

		@Override
		public Node parse(Parser parser) throws IOException, CarrotException {
			return parser.parseTag();
		}
	};

	// private static final ParseMode ECHO = new TagParseMode('}',
	// NodeType.ECHO);

	private static final ParseMode COMMENT = new ParseMode() {

		@Override
		public Node parse(Parser parser) throws IOException, CarrotException {
			return parser.skipCommentAndParseNext();
		}
	};

	private static final ParseMode FIXED = new ParseMode() {

		@Override
		public Node parse(Parser parser) throws IOException, CarrotException {
			return parser.parseNext();
		}
	};

	private static final ParseMode END = new ParseMode() {

		@Override
		public Node parse(Parser parser) throws IOException, CarrotException {
			return null;
		}
	};

	private ParseMode mode = FIXED;

	// private String parseTag(char end) throws IOException, CarrotException {
	// StringBuilder content = new StringBuilder();
	// for (;;) {
	// int c = reader.read();
	// switch (c) {
	// case -1:
	// return endSegment(content, END);
	// case '%':
	// case '#':
	// case '}':
	// int c2 = reader.read();
	// if (c2 == '}') {
	// if (c != end) {
	// throw new CarrotException("Expected " + end + "}, was " + c2 + "}");
	// }
	// return endSegment(content, LITERAL);
	// }
	// reader.unread(c2);
	// default:
	// content.append((char) c);
	// }
	// }
	// }

	private String parseFixed() throws IOException {
		StringBuilder content = new StringBuilder();
		for (;;) {
			int c = reader.read();
			switch (c) {
				case -1:
					mode = END;
					return content.toString();
				case '{':
					int c2 = reader.read();
					switch (c2) {
						case -1:
							mode = END;
							return content.append((char) c).toString();
						case '{':
							mode = ECHO;
							return content.toString();
						case '%':
							mode = TAG;
							return content.toString();
						case '#':
							mode = COMMENT;
							return content.toString();
						default:
							reader.unread(c2);
					}
				default:
					content.append((char) c);
			}
		}
	}

	private Node parseNext() throws IOException, CarrotException {
		String content = parseFixed();
		return !content.isEmpty() ? new FixedNode(content) : getNext();
	}

	private Node skipCommentAndParseNext() throws IOException, CarrotException {
		for (;;) {
			int c = reader.read();
			switch (c) {
				case -1:
					throw new CarrotException("unclosed comment");
				case '#':
					int c2 = reader.read();
					if (c2 == '}') {
						return getNext();
					}
					reader.unread(c2);
					break;
				default:
			}
		}
	}

}
