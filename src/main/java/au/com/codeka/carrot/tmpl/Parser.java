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

	public Node next() throws IOException, CarrotException {
		return mode.parse(this);
	}

	private enum ParseMode {
		TEXT {

			@Override
			Node parse(Parser parser) throws IOException, CarrotException {
				return parser.parseNext();
			}
		},
		TAG {

			@Override
			Node parse(Parser parser) throws IOException, CarrotException {
				return parser.parseTag();
			}
		},
		ECHO {

			@Override
			Node parse(Parser parser) throws IOException, CarrotException {
				return parser.parseEcho();
			}
		},
		COMMENT {

			@Override
			Node parse(Parser parser) throws IOException, CarrotException {
				return parser.skipCommentAndParseNext();
			}
		},
		END {

			@Override
			Node parse(Parser parser) throws IOException, CarrotException {
				// Do something similar with tokens?
				return MarkerNode.END_DOCUMENT;
			}
		};

		abstract Node parse(Parser parser) throws IOException, CarrotException;
	}

	private ParseMode mode = ParseMode.TEXT;

	private Node parseNext() throws IOException, CarrotException {
		String content = parseText();
		return !content.isEmpty() ? new TextNode(content) : next();
	}

	private String parseText() throws IOException {
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
							mode = ParseMode.END;
							return content.append((char) c).toString();
						case '<':
							mode = ParseMode.ECHO;
							return content.toString();
						case '%':
							mode = ParseMode.TAG;
							return content.toString();
						case '#':
							mode = ParseMode.COMMENT;
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
						return parseNext();
					}
					reader.unread(c2);
					break;
				default:
			}
		}
	}

}
