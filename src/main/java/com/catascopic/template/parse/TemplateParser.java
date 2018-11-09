package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Assigner;
import com.catascopic.template.PositionReader;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.catascopic.template.parse.Variables.Names;

public class TemplateParser {

	public static Node parse(Reader reader) throws IOException {
		return new TemplateParser(reader).parseRoot();
	}

	private final PositionReader reader;
	private Mode mode = Mode.TEXT;
	private Node node;

	TemplateParser(Reader reader) {
		this.reader = new PositionReader(reader, 1);
	}

	// @formatter:off
	private NodeCreator parseNext() throws IOException {
		switch (mode) {
		case TEXT:    return parseTextOrTag();
		case BREAK:   return breakNode();
		case TAG:     return parseTag();
		case EVAL:    return parseEval();
		case COMMENT: return skipCommentAndParseNext();
		case END:     return result(NodeResult.END_DOCUMENT);
		default:      throw new IllegalArgumentException(mode.name());
		}
	}
	// @formatter:on

	Node getNode() {
		if (node == null) {
			throw new IllegalStateException();
		}
		return node;
	}

	private NodeCreator parseTextOrTag() throws IOException {
		String content = parseContent();
		return !content.isEmpty()
				? result(NodeResult.NODE, new ContentNode(content))
				: parseNext();
	}

	private NodeCreator breakNode() {
		mode = Mode.TEXT;
		return BreakNode.INSTANCE;
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
				mode = Mode.BREAK;
				break loop;
			default:
				builder.append((char) ch);
			}
		}
		return builder.toString();
	}

	private NodeCreator parseTag() throws IOException {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.TAG);
		String tagName = tokenizer.parseIdentifier();
		mode = Mode.TEXT;
		switch (tagName) {
		case "if":
			return parseIf(tokenizer);
		case "else":
			return parseElse(tokenizer);
		case "for":
			return parseFor(tokenizer);
		case "set":
			return parseSet(tokenizer);
		case "template":
			return parseTemplate(tokenizer);
		case "text":
			return parseText(tokenizer);
		case "end":
			tokenizer.end();
			return SpecialNode.END;
		default:
			throw new TemplateParseException(reader,
					"unknown tag: %s", tagName);
		}
	}

	private static NodeCreator parseElse(Tokenizer tokenizer) throws IOException {
		if (tokenizer.tryConsume("if")) {
			return parseIf(tokenizer);
		}
		tokenizer.end();
		return new BlockNode(parseBlock(false));
	}

	private static NodeCreator parseFor(Tokenizer tokenizer) throws IOException {
		Names names = Variables.parseNames(tokenizer);
		tokenizer.consumeIdentifier("in");
		Term sequence = tokenizer.parseExpression();
		tokenizer.end();
		Block block = parseBlock(false);
		return new ForNode(names, sequence, block);
	}

	private static NodeCreator parseSet(Tokenizer tokenizer) {
		Assigner vars = Variables.parseAssignment(tokenizer);
		tokenizer.end();
		return new SetNode(vars);
	}

	private static NodeCreator parseTemplate(Tokenizer tokenizer) {
		Term templateName = tokenizer.parseExpression();
		Assigner vars;
		if (tokenizer.tryConsume("with")) {
			vars = Variables.parseAssignment(tokenizer);
		} else {
			vars = Variables.EMPTY;
		}
		tokenizer.end();
		return new TemplateNode(templateName, vars);
	}

	private static Node parseText(Tokenizer tokenizer) {
		Term textFileName = tokenizer.parseExpression();
		tokenizer.end();
		return new TextNode(textFileName);
	}

	private NodeCreator parseEval() {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.EVAL);
		Term evaluable = tokenizer.parseExpression();
		tokenizer.end();
		mode = Mode.TEXT;
		return result(NodeResult.NODE, new EvalNode(evaluable));
	}

	private NodeCreator skipCommentAndParseNext() throws IOException {
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
		throw new TemplateParseException(reader, "unclosed comment");
	}

	private enum Mode {

		TEXT,
		BREAK,
		TAG,
		EVAL,
		COMMENT,
		END
	}

	private enum NodeType {

		TEXT,
		WHITESPACE,
		NODE,
		END_DOCUMENT;
	}

}
