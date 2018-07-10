package com.catascopic.template.parse;

class NodeResult {

	private static final NodeResult END_DOCUMENT = new NodeResult(
			Type.END_DOCUMENT);
	private static final NodeResult END_TAG = new NodeResult(Type.END_TAG);

	public static NodeResult node(Node node) {
		return new NodeResult(Type.NODE, node);
	}

	public static NodeResult elseNode(Node node) {
		return new NodeResult(Type.ELSE, node);
	}

	public static NodeResult endTag() {
		return END_TAG;
	}

	public static NodeResult endDocument() {
		return END_DOCUMENT;
	}

	private final Type type;
	private final Node node;

	NodeResult(Type type, Node node) {
		this.type = type;
		this.node = node;
	}

	private NodeResult(Type type) {
		this(type, null);
	}

	Type type() {
		return type;
	}

	Node getNode() {
		if (node == null) {
			throw new IllegalStateException(type + " does not have a node");
		}
		return node;
	}

	enum Type {
		NODE,
		ELSE,
		END_TAG,
		END_DOCUMENT;
	}

}
