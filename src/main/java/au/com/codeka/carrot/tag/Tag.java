package au.com.codeka.carrot.tag;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.StatementParser;
import au.com.codeka.carrot.tmpl.TagNode;

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
}
