package au.com.codeka.carrot.tmpl;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.tmpl.parse.Content;
import au.com.codeka.carrot.tmpl.parse.ContentType;
import au.com.codeka.carrot.tmpl.parse.ContentParser;

/**
 * Parses a stream of {@link Content}s into a tree of {@link Node}s.
 */
public class TemplateParser {
	
	private final Configuration config;

	public TemplateParser(Configuration config) {
		this.config = config;
	}

	public Node parse(ContentParser tokenizer) throws CarrotException {
		Node root = new RootNode();
		parse(tokenizer, root);
		return root;
	}

	/** Parse tokens into the given {@link Node}. */
	private void parse(ContentParser tokenizer, Node node) throws CarrotException {
		Node current = node;
		while (true) {
			Content token = tokenizer.getNext();
			if (token == null) {
				// Note if there's any open blocks right now, we just assume
				// they end at the end of the file.
				return;
			}

			Node childNode;
			if (token.getType() == ContentType.COMMENT) {
				// Just ignore this token.
				childNode = null;
			} else if (token.getType() == ContentType.ECHO) {
				childNode = TagNode.createEcho(token, config);
			} else if (token.getType() == ContentType.TAG) {
				TagNode tagNode = TagNode.create(token, config);
				if (tagNode.isEndBlock()) {
					return;
				} else if (current.canChain(tagNode)) {
					// If we can chain to the given node, then instead of adding
					// it as child, we'll chain to it instead.
					current = current.chain(tagNode);
					childNode = null;
				} else {
					childNode = tagNode;
				}
			} else if (token.getType() == ContentType.FIXED) {
				childNode = FixedNode.create(token);
			} else {
				throw new IllegalStateException("Unknown token type: " + token.getType());
			}
			if (childNode != null) {
				if (childNode.isBlockNode()) {
					parse(tokenizer, childNode);
				}
				current.add(childNode);
			}
		}
	}

}
