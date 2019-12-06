package com.catascopic.template.parse;

import java.io.IOException;
import java.util.List;

import com.catascopic.template.TemplateParseException;
import com.catascopic.template.TrackingReader;
import com.catascopic.template.expr.Symbol;
import com.catascopic.template.expr.Tokenizer;
import com.google.common.base.CharMatcher;

class TagParser {

	static List<Tag> parse(TrackingReader reader) throws IOException {
		return new TagParser(reader).parse();
	}

	private List<Tag> parse() throws IOException {
		do {
			parseNext();
		} while (mode != Mode.END);
		tags.endDocument();
		return tags.result();
	}

	private TrackingReader reader;
	private TagCleaner tags = new TagCleaner();
	private Mode mode = Mode.TEXT;

	TagParser(TrackingReader reader) {
		this.reader = reader;
	}

	void setMode(Mode mode) {
		this.mode = mode;
	}

	private void parseNext() throws IOException {
		switch (mode) {
		case TEXT:
			parseTextOrTag();
			break;
		case NEWLINE:
			newline();
			break;
		case STATEMENT:
			parseStatement();
			break;
		case EVAL:
			parseEval();
			break;
		case COMMENT:
			skipCommentAndParseNext();
			break;
		case END:
			break;
		default:
			throw new IllegalArgumentException(mode.name());
		}
	}

	private void parseTextOrTag() throws IOException {
		String text = parseContent();
		if (text.isEmpty()) {
			parseNext();
		} else {
			Tag tag = TextNode.getTag(text);
			if (CharMatcher.whitespace().matchesAllOf(text)) {
				tags.whitespace(tag);
			} else {
				tags.text(tag);
			}
		}
	}

	private String parseContent() throws IOException {
		StringBuilder builder = new StringBuilder();
		loop: for (;;) {
			int ch = reader.read();
			switch (ch) {
			case -1:
				mode = Mode.END;
				break loop;
			case '\n':
				mode = Mode.NEWLINE;
				break loop;
			case '@':
				if (getMode(Mode.STATEMENT)) {
					break loop;
				}
				break;
			case '$':
				if (getMode(Mode.EVAL)) {
					break loop;
				}
				break;
			case '#':
				if (getMode(Mode.COMMENT)) {
					break loop;
				}
				break;
			default:
			}
			builder.append((char) ch);
		}
		return builder.toString();
	}

	private boolean getMode(Mode mode) throws IOException {
		if (reader.tryRead('{')) {
			this.mode = mode;
			return true;
		}
		return false;
	}

	private void parseStatement() {
		Tokenizer tokenizer = new Tokenizer(reader);
		Tag tag = getTag(tokenizer);
		tags.statement(tag);
		tokenizer.consume(Symbol.RIGHT_CURLY_BRACKET);
		mode = Mode.TEXT;
	}

	private void newline() {
		tags.endLine();
		mode = Mode.TEXT;
	}

	private static Tag getTag(Tokenizer tokenizer) {
		String tagName = tokenizer.parseIdentifier();
		switch (tagName) {
		case "if":
			return IfNode.parseTag(tokenizer);
		case "else":
			return IfNode.parseElseTag(tokenizer);
		case "for":
			return ForNode.parseTag(tokenizer);
		case "set":
			return SetNode.parseTag(tokenizer);
		case "setblock":
			return SetBlockNode.parseTag(tokenizer);
		case "print":
			return PrintNode.getTag(tokenizer);
		case "end":
			return new EndTag(tokenizer.getLocation());
		default:
			throw new TemplateParseException(tokenizer, "unknown tag: %s", tagName);
		}
	}

	private void parseEval() {
		Tokenizer tokenizer = new Tokenizer(reader);
		tags.text(EvalNode.getTag(tokenizer));
		tokenizer.consume(Symbol.RIGHT_CURLY_BRACKET);
		mode = Mode.TEXT;
	}

	private void skipCommentAndParseNext() throws IOException {
		for (;;) {
			int c = reader.read();
			switch (c) {
			case -1:
				throw new TemplateParseException(reader, "unclosed comment");
			case '}':
				tags.comment();
				mode = Mode.TEXT;
				parseNext();
				return;
			default:
			}
		}
	}

	private enum Mode {

		TEXT,
		NEWLINE,
		STATEMENT,
		EVAL,
		COMMENT,
		END
	}

}
