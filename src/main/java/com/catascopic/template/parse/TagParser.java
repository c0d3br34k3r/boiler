package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.catascopic.template.PositionReader;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.eval.Symbol;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.base.CharMatcher;

// TODO: re-modularize TagParser, TagCleaner, and TemplateParser
class TagParser {

	static List<Tag> parse(Reader reader) throws IOException {
		return new TagParser(reader).parse();
	}

	private List<Tag> parse() throws IOException {
		do {
			parseNext();
		} while (mode != Mode.END);
		tags.endDocument();
		return tags.result();
	}

	private final PositionReader reader;
	private TagCleaner tags = new TagCleaner();
	private Mode mode = Mode.TEXT;

	TagParser(Reader reader) {
		this.reader = new PositionReader(reader, 1);
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
		case TAG:
			parseTag();
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
			case '$':
			case '#':
				if (reader.tryRead('{')) {
					mode = getMode(ch);
					break loop;
				}
				// fallthrough
			default:
				builder.append((char) ch);
			}
		}
		return builder.toString();
	}

	private static Mode getMode(int ch) {
		switch (ch) {
		case '@':
			return Mode.TAG;
		case '$':
			return Mode.EVAL;
		case '#':
			return Mode.COMMENT;
		}
		throw new AssertionError();
	}

	private void parseTag() {
		Tokenizer tokenizer = new Tokenizer(reader);
		Tag tag = getTag(tokenizer);
		tags.instruction(tag);
		tokenizer.consume(Symbol.RIGHT_CURLY_BRACKET);
		mode = Mode.TEXT;
	}

	private void newline() {
		tags.endLine(NewlineNode.NEWLINE);
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
		case "print":
			return PrintNode.getTag(tokenizer);
		// TODO: revisit
		// case "template":
		// return TemplateNode.parseTag(tokenizer);
		// case "textfile":
		// return TextFileNode.parseTag(tokenizer);
		case "end":
			return EndTag.END;
		default:
			throw new TemplateParseException(tokenizer,
					"unknown tag: %s", tagName);
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
		TAG,
		EVAL,
		COMMENT,
		END
	}

}
