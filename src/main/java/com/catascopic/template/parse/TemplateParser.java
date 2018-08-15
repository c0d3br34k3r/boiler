package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.PositionReader;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.parse.Variables.Assigner;
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

	NodeResult parseNext() throws IOException {
		return mode.parse(this);
	}

	Node getNode() {
		if (node == null) {
			throw new IllegalStateException();
		}
		return node;
	}

	private NodeResult parseTextOrTag() throws IOException {
		String content = parseContent();
		return !content.isEmpty()
				? result(NodeResult.NODE, new ContentNode(content))
				: parseNext();
	}

	private NodeResult breakNode() {
		mode = Mode.TEXT;
		return result(NodeResult.NODE, BreakNode.INSTANCE);
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
			case '\n':
				mode = Mode.BREAK;
				break loop;
			default:
				builder.append((char) ch);
			}
		}
		return builder.toString();
	}

	private NodeResult parseTag() throws IOException {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.TAG);
		String tagName = tokenizer.parseIdentifier();
		mode = Mode.TEXT;
		switch (tagName) {
		case "if":
			return result(NodeResult.NODE, parseIf(tokenizer));
		case "else":
			return result(NodeResult.ELSE, parseElse(tokenizer));
		case "for":
			return result(NodeResult.NODE, parseFor(tokenizer));
		case "set":
			return result(NodeResult.NODE, parseSet(tokenizer));
		case "template":
			return result(NodeResult.NODE, parseTemplate(tokenizer));
		case "text":
			return result(NodeResult.NODE, parseText(tokenizer));
		case "end":
			tokenizer.end();
			return result(NodeResult.END_TAG);
		default:
			throw reader.parseError("unknown tag: %s", tagName);
		}
	}

	private Node parseIf(Tokenizer tokenizer) throws IOException {
		Term condition = tokenizer.parseExpression();
		tokenizer.end();
		Block block = parseBlock(true);
		return new IfNode(condition, block);
	}

	private Node parseElse(Tokenizer tokenizer) throws IOException {
		if (tokenizer.tryConsume("if")) {
			return parseIf(tokenizer);
		}
		tokenizer.end();
		return new BlockNode(parseBlock(false));
	}

	private Node parseFor(Tokenizer tokenizer) throws IOException {
		Names names = Variables.parseNames(tokenizer);
		tokenizer.consumeIdentifier("in");
		Term iterable = tokenizer.parseExpression();
		tokenizer.end();
		Block block = parseBlock(false);
		return new ForNode(names, iterable, block);
	}

	private static Node parseSet(Tokenizer tokenizer) {
		Assigner vars = Variables.parseAssignment(tokenizer);
		tokenizer.end();
		return new SetNode(vars);
	}

	private static Node parseTemplate(Tokenizer tokenizer) {
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

	private NodeResult parseEcho() {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.ECHO);
		Term term = tokenizer.parseExpression();
		tokenizer.end();
		mode = Mode.TEXT;
		return result(NodeResult.NODE, new EchoNode(term));
	}

	private Block parseBlock(boolean elseAllowed) throws IOException {
		List<Node> nodes = new ArrayList<>();
		for (;;) {
			NodeResult result = parseNext();
			switch (result) {
			case NODE:
				nodes.add(getNode());
				break;
			case ELSE:
				if (!elseAllowed) {
					throw reader.parseError("else not allowed");
				}
				return new Block(nodes, getNode());
			case END_TAG:
				return new Block(nodes);
			case END_DOCUMENT:
				throw reader.parseError("unclosed tag");
			}
		}
	}

	public Node parseRoot() throws IOException {
		List<Node> nodes = new ArrayList<>();
		for (;;) {
			NodeResult result = parseNext();
			switch (result) {
			case NODE:
				nodes.add(getNode());
				break;
			case ELSE:
			case END_TAG:
				throw reader.parseError("unbalanced %s tag", result);
			case END_DOCUMENT:
				return new BlockNode(new Block(nodes));
			}
		}
	}

	private NodeResult skipCommentAndParseNext() throws IOException {
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
		throw reader.parseError("unclosed comment");
	}

	private NodeResult endDocument() {
		return result(NodeResult.END_DOCUMENT);
	}

	private NodeResult result(NodeResult type, Node node) {
		this.node = node;
		return type;
	}

	private NodeResult result(NodeResult type) {
		this.node = null;
		return type;
	}

	private enum Mode {
		TEXT {

			@Override
			NodeResult parse(TemplateParser parser) throws IOException {
				return parser.parseTextOrTag();
			}
		},
		BREAK {

			@Override
			NodeResult parse(TemplateParser parser) throws IOException {
				return parser.breakNode();
			}
		},
		TAG {

			@Override
			NodeResult parse(TemplateParser parser) throws IOException {
				return parser.parseTag();
			}
		},
		ECHO {

			@Override
			NodeResult parse(TemplateParser parser) throws IOException {
				return parser.parseEcho();
			}
		},
		COMMENT {

			@Override
			NodeResult parse(TemplateParser parser) throws IOException {
				return parser.skipCommentAndParseNext();
			}
		},
		END {

			@Override
			NodeResult parse(TemplateParser parser) throws IOException {
				return parser.endDocument();
			}
		};

		abstract NodeResult parse(TemplateParser parser) throws IOException;
	}

	private enum NodeResult {

		NODE,
		ELSE,
		END_TAG,
		END_DOCUMENT;
	}

}
