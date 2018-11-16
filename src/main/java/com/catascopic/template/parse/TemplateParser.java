package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.PositionReader;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.base.CharMatcher;

public class TemplateParser {

	public static Node parse(Reader reader) throws IOException {
		TemplateParser parser = new TemplateParser(reader);
		parser.parse();
		return null;
	}

	private void parse() throws IOException {
		Tag tag;
		do {
			tag = parseNext();
		} while (tags.add(tag));
	}

	private final PositionReader reader;
	private List<Tag> tags = new ArrayList<>();
	private List<Tag> lineBuffer = new ArrayList<>();
	private Tag onlyInstruction;
	private int instructionTagCount;
	private Mode mode = Mode.TEXT;

	TemplateParser(Reader reader) {
		this.reader = new PositionReader(reader, 1);
	}

	private boolean parseNext() throws IOException {
		switch (mode) {
		case TEXT:
			return parseTextOrTag();
		case NEWLINE:
			return newline();
		case TAG:
			return parseTag();
		case EVAL:
			return parseEval();
		case COMMENT:
			return skipCommentAndParseNext();
		case END:
			return SpecialNode.END_DOCUMENT;
		default:
			throw new IllegalArgumentException(mode.name());
		}
	}

	private boolean parseTextOrTag() throws IOException {
		String text = parseContent();
		if (text.isEmpty()) {
			return parseNext();
		}
		Tag textNode = new TextNode(text);
		if (!CharMatcher.whitespace().matchesAllOf(text)) {
			instructionTagCount = 2;
		}
		lineBuffer.add(textNode);
		return true;
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

	private Tag parseTag() {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.TAG);
		Tag tag = getTag(tokenizer);
		tokenizer.end();
		mode = Mode.TEXT;
		return tag;
	}

	private boolean newline() {
		mode = Mode.TEXT;
		if (instructionTagCount == 1) {
			tags.add(onlyInstruction);
		}
		lineBuffer = new ArrayList<>();
		instructionTagCount = 0;
		return true;
	}

	private static Tag getTag(Tokenizer tokenizer) {
		String tagName = tokenizer.parseIdentifier();
		switch (tagName) {
		case "if":
			return IfNode.parseTag(tokenizer);
		case "else":
			return parseElse(tokenizer);
		case "for":
			return ForNode.parseTag(tokenizer);
		case "set":
			return SetNode.parseTag(tokenizer);
		case "template":
			return TemplateNode.parseTag(tokenizer);
		case "textfile":
			return TextFileNode.parseTag(tokenizer);
		case "end":
			return SpecialNode.END;
		default:
			throw new TemplateParseException(tokenizer,
					"unknown tag: %s", tagName);
		}
	}

	private Tag parseEval() {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.EVAL);
		Term evaluable = tokenizer.parseExpression();
		tokenizer.end();
		mode = Mode.TEXT;
		return new EvalNode(evaluable);
	}

	private boolean skipCommentAndParseNext() throws IOException {
		loop: for (;;) {
			int c = reader.read();
			switch (c) {
			case -1:
				break loop;
			case '#':
				int c2 = reader.read();
				switch (c2) {
				case '>':
					return parseTextOrTag();
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
