package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import javax.annotation.Nullable;
import javax.swing.text.Segment;

import com.google.common.io.LineReader;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.TagType;

public class Parser {

	private final PushbackReader reader;

	public Parser(Reader reader) {
		this.reader = new PushbackReader(reader, 1);
	}

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

	private static final ParseMode TEXT = new ParseMode() {

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

	private ParseMode mode = TEXT;

	private Node parseNext() throws IOException, CarrotException {
		String content = parseFixed();
		return !content.isEmpty() ? new TextNode(content) : getNext();
	}

	private String parseFixed() throws IOException {
		StringBuilder content = new StringBuilder();
		for (;;) {
			int c = reader.read();
			switch (c) {
				case -1:
					mode = END;
					return content.toString();
				case '<':
					int c2 = reader.read();
					switch (c2) {
						case -1:
							mode = END;
							return content.append((char) c).toString();
						case '<':
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

	private Node skipCommentAndParseNext() throws IOException, CarrotException {
		for (;;) {
			int c = reader.read();
			switch (c) {
				case -1:
					throw new CarrotException("unclosed comment");
				case '#':
					int c2 = reader.read();
					if (c2 == '>') {
						return getNext();
					}
					reader.unread(c2);
					break;
				default:
			}
		}
	}


}
