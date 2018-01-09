package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import javax.annotation.Nullable;
import javax.swing.text.Segment;

import com.google.common.io.LineReader;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.TagType;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;

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
		StringBuilder builder = new StringBuilder();
		loop: for (;;) {
			int ch = reader.read();
			switch (ch) {
			case -1:
				mode = ParseMode.END;
				break loop;
			case '<':
				int ch2 = reader.read();
				switch (ch2) {
				case -1:
					mode = ParseMode.END;
					builder.append((char) ch);
					break loop;
				case '<':
					mode = ParseMode.ECHO;
					break loop;
				case '%':
					mode = ParseMode.TAG;
					break loop;
				case '#':
					mode = ParseMode.COMMENT;
					break loop;
				default:
					reader.unread(ch2);
				}
			default:
				builder.append((char) ch);
			}
		}
		return builder.toString();
	}

	private Node parseTag() throws CarrotException {
		Tokenizer tokenizer = new Tokenizer(reader);
		Node node;
		String tagName = tokenizer.parseIdentifier();
		switch (tagName) {
		case "if":

		case "else":

		case "for":

		case "echo":
			node = parseEcho(tokenizer);
			break;
		case "set":

		case "include":

		default:
			throw new CarrotException("unknown tag: " + tagName);
		}
		tokenizer.consume(TokenType.END);
		mode = ParseMode.TEXT;
		return node;
	}

	private Node parseEcho(Tokenizer tokenizer) {
		return new Echo(tokenizer.parseExpression());
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
