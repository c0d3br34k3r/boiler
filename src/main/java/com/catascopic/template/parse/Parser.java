package com.catascopic.template.parse;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.TemplateParseException;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.parse.Variables.Assigner;
import com.catascopic.template.parse.Variables.Names;

public class Parser {

	public static Node parse(Reader reader) throws IOException {
		// TODO: exceptions should contain location in template file
		return new Parser(reader).parseRoot();
	}

	private final PushbackReader reader;
	private Mode mode = Mode.TEXT;

	Parser(Reader reader) {
		this.reader = new PushbackReader(reader, 1);
	}

	NodeResult parseNext() throws IOException {
		return mode.parse(this);
	}

	private NodeResult parseTextOrTag() throws IOException {
		String content = parseContent();
		return !content.isEmpty()
				? NodeResult.node(new ContentNode(content))
				: parseNext();
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

	private NodeResult parseTag() throws IOException {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.TAG);
		String tagName = tokenizer.parseIdentifier();
		mode = Mode.TEXT;
		switch (tagName) {
		case "if":
			return NodeResult.node(parseIf(tokenizer));
		case "else":
			return NodeResult.elseNode(parseElse(tokenizer));
		case "for":
			return NodeResult.node(parseFor(tokenizer));
		case "set":
			return NodeResult.node(parseSet(tokenizer));
		case "template":
			return NodeResult.node(parseTemplate(tokenizer));
		case "text":
			return NodeResult.node(parseText(tokenizer));
		case "end":
			tokenizer.end();
			return NodeResult.endTag();
		default:
			throw new TemplateParseException("unknown tag: " + tagName);
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

	private Node parseEcho() {
		Tokenizer tokenizer = new Tokenizer(reader, Tokenizer.Mode.ECHO);
		Term term = tokenizer.parseExpression();
		tokenizer.end();
		mode = Mode.TEXT;
		return new Echo(term);
	}

	private Block parseBlock(boolean elseAllowed) throws IOException {
		List<Node> nodes = new ArrayList<>();
		for (;;) {
			NodeResult result = parseNext();
			switch (result.type()) {
			case NODE:
				nodes.add(result.getNode());
				break;
			case ELSE:
				if (!elseAllowed) {
					throw new TemplateParseException("else not allowed");
				}
				return new Block(nodes, result.getNode());
			case END_TAG:
				return new Block(nodes);
			case END_DOCUMENT:
				throw new TemplateParseException("unclosed tag");
			}
		}
	}

	public Node parseRoot() throws IOException {
		List<Node> nodes = new ArrayList<>();
		for (;;) {
			NodeResult result = parseNext();
			switch (result.type()) {
			case NODE:
				nodes.add(result.getNode());
				break;
			case ELSE:
			case END_TAG:
				throw new TemplateParseException("unbalanced %s tag",
						result.type());
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
		throw new TemplateParseException("unclosed comment");
	}

	private enum Mode {
		TEXT {

			@Override
			NodeResult parse(Parser parser) throws IOException {
				return parser.parseTextOrTag();
			}
		},
		TAG {

			@Override
			NodeResult parse(Parser parser) throws IOException {
				return parser.parseTag();
			}
		},
		ECHO {

			@Override
			NodeResult parse(Parser parser) throws IOException {
				return NodeResult.node(parser.parseEcho());
			}
		},
		COMMENT {

			@Override
			NodeResult parse(Parser parser) throws IOException {
				return parser.skipCommentAndParseNext();
			}
		},
		END {

			@Override
			NodeResult parse(Parser parser) throws IOException {
				return NodeResult.endDocument();
			}
		};

		abstract NodeResult parse(Parser parser) throws IOException;
	}

}
