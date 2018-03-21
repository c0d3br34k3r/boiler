package com.catascopic.template.tag2;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.StatementParser;
import com.catascopic.template.tag.IncludeTag;
import com.catascopic.template.tag.SetNode;
import com.catascopic.template.tag.TagNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Interface that tags must implement.
 *
 * <p>
 * A tag is a keyword that appears at the beginning of a {% %} block. The tag
 * must have a single-word, lower-case name. If the tag can itself contain
 * content, it must specify the name of the end-tag which ends the tag.
 * </p>
 */
public abstract class Tag {

	/**
	 * Parse the statement that appears after the tag in the markup. This is
	 * guaranteed to be called before {@link #isBlockTag()} or
	 * {@link #canChain(Tag)}.
	 *
	 * @param stmtParser A {@link StatementParser} for parsing the statement.
	 * @throws CarrotException if there is an unrecoverable error parsing the
	 *         statement.
	 */
	public void parseStatement(StatementParser stmtParser) throws CarrotException {
		stmtParser.parseEnd();
	}

	/**
	 * @return True if this is a "block" tag, meaning it contains child content
	 *         (in the form of a list of Nodes) and false if this is not a block
	 *         tag (e.g. it's just a single inline element or something).
	 */
	public boolean isBlockTag() {
		return false;
	}

	/**
	 * Return true if we can chain to the given next {@link Tag}.
	 *
	 * @param nextTag The next {@link Tag} that we want to check whether we can
	 *        chain to.
	 * @return True if we can chain to the next tag.
	 */
	public boolean canChain(Tag nextTag) {
		return false;
	}

	/**
	 * Render this {@link Tag} to the given {@link Writer}.
	 *
	 * @param engine The current {@link CarrotEngine}.
	 * @param writer The {@link Writer} to render to.
	 * @param tagNode The {@link TagNode} that we're enclosed in. You can use
	 *        this to render the children, or query the children or whatever.
	 * @param scope The current {@link Scope}.
	 * @throws CarrotException if there's an error parsing or rendering the
	 *         template
	 * @throws IOException when there's an error writing to the {@link Writer}
	 *         (basically this is just passed on from whatever errors the
	 *         {@link Writer} might throw..
	 */
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException, IOException {}

	private static final Map<String, TagCreator> LOOKUP;

	static {
		Builder<String, TagCreator> builder = ImmutableMap.builder();
		for (TagCreator type : TagCreator.values()) {
			builder.put(type.tagName, type);
		}
		LOOKUP = builder.build();
	}

	public static Tag create(String name) {
		return LOOKUP.get(name).create();
	}

	enum TagCreator {

		ECHO("echo") {

			@Override
			Tag create() {
				return new EchoTag();
			}
		},

		IF("if") {

			@Override
			Tag create() {
				return new IfTag();
			}
		},

		FOR("for") {

			@Override
			Tag create() {
				return new ForTag();
			}
		},

		ELSE("else") {

			@Override
			Tag create() {
				return new ElseTag();
			}
		},

		SET("set") {

			@Override
			Tag create() {
				return new SetNode();
			}
		},

		INCLUDE("include") {

			@Override
			Tag create() {
				return new IncludeTag();
			}
		},

		END("end") {

			@Override
			Tag create() {
				return EndTag.END;
			}
		};

		private final String tagName;

		TagCreator(String tagName) {
			this.tagName = tagName;
		}

		abstract Tag create();
	}

}
