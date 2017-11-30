package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import com.google.common.base.Supplier;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.StatementParser;
import au.com.codeka.carrot.expr.Tokenizer;
import au.com.codeka.carrot.tag.EndTag;
import au.com.codeka.carrot.tag.Tag;
import au.com.codeka.carrot.tmpl.parse.Token;

/**
 * A {@link TagNode} represents a node of the form "{% tagname foo %}" where
 * "tagname" is the name of the tag and "foo" is the parameters.
 * <p>
 * Tags are represented by the {@link Tag} class, which is extensible.
 */
public class TagNode extends Node {

	private final Tag tag;

	public TagNode(Tag tag) {
		super(tag.isBlockTag());
		this.tag = tag;
	}

	/**
	 * Creates a special {@link TagNode} for an echo token.
	 *
	 * @param token The {@link Token} the echo node is going to created from,
	 *        must have {@link Token#getType()} of
	 *        {@link au.com.codeka.carrot.tmpl.parse.TokenType#ECHO}.
	 * @param config The current {@link Configuration}.
	 * @return A new {@link TagNode}.
	 * @throws CarrotException if there's a problem parsing the token.
	 */
	public static TagNode createEcho(Token token, Configuration config) throws CarrotException {
		return create("echo", token.getValue(), config);
	}

	/**
	 * Creates a {@link TagNode} for the given {@link Token}.
	 *
	 * @param token The {@link Token} the node is going to created from, must
	 *        have {@link Token#getType()} of
	 *        {@link au.com.codeka.carrot.tmpl.parse.TokenType#TAG}.
	 * @param config The current {@link Configuration}.
	 * @return A new {@link TagNode}.
	 * @throws CarrotException if there's a problem parsing the token.
	 */
	public static TagNode create(Token token, Configuration config) throws CarrotException {
		String content = token.getValue().trim();
		String tagName;
		int space = content.indexOf(' ');
		if (space <= 0) {
			tagName = content;
			content = "";
		} else {
			tagName = content.substring(0, space);
			content = content.substring(space).trim();
		}
		return create(tagName, content, config);
	}

	private static TagNode create(String tagName, String content, Configuration config)
			throws CarrotException {
		Tag tag = config.getTagRegistry().createTag(tagName);
		if (tag == null) {
			throw new CarrotException(String.format("Invalid tag '%s'", tagName));
		}
		try {
			StatementParser stmtParser =
					new StatementParser(new Tokenizer(new StringReader(content)));
			tag.parseStatement(stmtParser);
		} catch (CarrotException e) {
			throw new CarrotException(
					"Exception parsing statement for '" + tagName + "' [" + content + "]", e);
		}
		return new TagNode(tag);
	}
	
//	private static TagNode create(Supplier<Tag> creator, String content, Configuration config)
//			throws CarrotException {
//		Tag tag = config.getTagRegistry().createTag(tagName);
//		if (tag == null) {
//			throw new CarrotException(String.format("Invalid tag '%s'", tagName));
//		}
//		try {
//			StatementParser stmtParser =
//					new StatementParser(new Tokenizer(new StringReader(content)));
//			tag.parseStatement(stmtParser);
//		} catch (CarrotException e) {
//			throw new CarrotException(
//					"Exception parsing statement for '" + tagName + "' [" + content + "]", e);
//		}
//		return new TagNode(tag);
//	}

	@Override
	public boolean canChain(Node nextNode) {
		if (nextNode instanceof TagNode) {
			Tag nextTag = ((TagNode) nextNode).tag;
			return tag.canChain(nextTag);
		}
		return false;
	}

	/**
	 * @return True if this is an end block (that is, if it ends it's parent's
	 *         block).
	 */
	public boolean isEndBlock() {
		return tag instanceof EndTag;
	}

	/**
	 * @return The {@link Tag} for this node.
	 */
	public Tag getTag() {
		return tag;
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		tag.render(engine, writer, this, scope);
	}

}
