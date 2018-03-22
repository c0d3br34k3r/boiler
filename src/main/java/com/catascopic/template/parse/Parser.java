package com.catascopic.template.parse;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.List;

import com.catascopic.template.TemplateParseException;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.parse.Variables.Assigner;

public class Parser {

	private final PushbackReader reader;
	private Mode mode = Mode.TEXT;

	public Parser(Reader reader) {
		this.reader = new PushbackReader(reader, 1);
	}

	private Node next2() throws IOException {
		switch (mode) {
		case TEXT:
			return parseTextOrTag();
		case TAG:
			return parseTag();
		case ECHO:
			return parseEcho();
		case COMMENT:
			return skipCommentAndParseNext();
		case END:
			// TODO:
		default:
			throw new AssertionError();
		}
	}

	private enum Mode {
		TEXT {
			@Override
			Node parse(Parser parser) throws IOException {
				return parser.parseTextOrTag();
			}
		},
		TAG {
			@Override
			Node parse(Parser parser) throws IOException {
				return parser.parseTag();
			}
		},
		ECHO {
			@Override
			Node parse(Parser parser) throws IOException {
				return parser.parseEcho();
			}
		},
		COMMENT {
			@Override
			Node parse(Parser parser) throws IOException {
				return parser.skipCommentAndParseNext();
			}
		},
		END {
			@Override
			Node parse(Parser parser) throws IOException {
				// Do something similar with tokens?
				return MarkerNode.END_DOCUMENT;
			}
		};

		abstract Node parse(Parser parser) throws IOException;
	}

	public Node parseNext() throws IOException {
		return mode.parse(this);
	}

	private Node parseTextOrTag() throws IOException {
		String content = parseContent();
		return !content.isEmpty() ? new ContentNode(content) : parseNext();
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
					mode = Mode.ECHO;
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
			default:
				builder.append((char) ch);
			}
		}
		return builder.toString();
	}

	private Node parseTag() throws IOException {
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
		case "echo":
			return parseEcho(tokenizer);
		case "set":
			return parseSet(tokenizer);
		case "template":
			return parseTemplate(tokenizer);
		case "text":
			return parseText(tokenizer);
		case "end":
			// TODO:
		default:
			throw new TemplateParseException("unknown tag: " + tagName);
		}
	}

	private Node parseIf(Tokenizer tokenizer) {
		Term condition = tokenizer.parseExpression();
		tokenizer.end();
		Block block = parseBlock(true);
		return new IfNode(condition, block);
	}

	private Node parseElse(Tokenizer tokenizer) {
		if (tokenizer.tryConsume("if")) {
			return parseIf(tokenizer);
		}
		tokenizer.end();
		return new LastElseNode(parseBlock(false));
	}

	private Node parseFor(Tokenizer tokenizer) {
		List<String> varNames = Variables.parseNames(tokenizer);
		tokenizer.consumeIdentifier("in");
		Term iterable = tokenizer.parseExpression();
		tokenizer.end();
		Block block = parseBlock(false);
		if (varNames.size() == 1) {
			return new ForNode(varNames.get(0), iterable, block);
		}
		return new UnpackForNode(varNames, iterable, block);
	}

	private Node parseEcho() {
		return parseEcho(new Tokenizer(reader, Tokenizer.Mode.ECHO));
	}

	private static Node parseEcho(Tokenizer tokenizer) {
		Term term = tokenizer.parseExpression();
		tokenizer.end();
		return new Echo(term);
	}

	private static Node parseSet(Tokenizer tokenizer) {
		Assigner vars = Variables.parse(tokenizer);
		tokenizer.end();
		return new SetNode(vars);
	}

	private static Node parseTemplate(Tokenizer tokenizer) {
		Term templateName = tokenizer.parseExpression();
		Variables vars;
		if (tokenizer.tryConsume("with")) {
			vars = Variables.parse(tokenizer);
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

	private Block parseBlock(boolean chainElse) {
		parseTag();
	}

	private Node skipCommentAndParseNext() throws IOException {
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
		throw new TemplateParseException("unclosed comment");
	}

}
