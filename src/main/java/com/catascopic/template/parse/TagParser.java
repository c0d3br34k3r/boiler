package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.catascopic.template.Location;
import com.catascopic.template.PositionReader;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.base.CharMatcher;

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
		TagCleaner.setLocation(reader.getLocation());
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
		Location location = reader.getLocation();
		String text = parseContent();
		if (text.isEmpty()) {
			parseNext();
		} else {
			Tag tag = TextNode.getTag(location, text);
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
			case '<':
				int ch2 = reader.read();
				switch (ch2) {
				case '<':
					mode = Mode.EVAL;
					break loop;
				case '%':
					mode = Mode.TAG;
					break loop;
				case '#':
					mode = Mode.COMMENT;
					break loop;
				case -1:
					break;
				default:
					reader.unread(ch2);
				}
				break;
			case '\n':
				mode = Mode.NEWLINE;
				break loop;
			default:
				builder.append((char) ch);
			}
		}
		return builder.toString();
	}

	private void parseTag() {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.TAG);
		Tag tag = getTag(tokenizer);
		tags.instruction(tag);
		tokenizer.end();
		mode = Mode.TEXT;
	}

	private void newline() {
		mode = Mode.TEXT;
		tags.endLine(NewlineNode.getTag(reader.getLocation()));
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
		// TODO: revisit
		// case "template":
		// return TemplateNode.parseTag(tokenizer);
		// case "textfile":
		// return TextFileNode.parseTag(tokenizer);
		case "end":
			return new EndTag(tokenizer.getLocation());
		default:
			throw new TemplateParseException(tokenizer,
					"unknown tag: %s", tagName);
		}
	}

	private void parseEval() {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.EVAL);
		tags.text(EvalNode.getTag(tokenizer));
		tokenizer.end();
		mode = Mode.TEXT;
	}

	private void skipCommentAndParseNext() throws IOException {
		loop: for (;;) {
			int c = reader.read();
			switch (c) {
			case -1:
				break loop;
			case '#':
				int c2 = reader.read();
				switch (c2) {
				case '>':
					parseTextOrTag();
					return;
				case -1:
					break loop;
				default:
					reader.unread(c2);
				}
				break;
			default:
			}
		}
		// TODO: locate beginning of comment
		throw new TemplateParseException(reader, "unclosed comment");
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
