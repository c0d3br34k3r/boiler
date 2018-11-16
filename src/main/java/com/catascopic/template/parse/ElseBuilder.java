package com.catascopic.template.parse;

abstract class ElseBuilder extends NodeBuilderTag {

	private NodeBuilder target;

	@Override
	void setElse(NodeBuilder elseBuilder) {
		if (target != null) {
			target.setElse(elseBuilder);
		} else {
			target = elseBuilder;
		}
	}

	@Override
	void add(Node node) {
		if (target == null) {
			super.add(node);
		} else {
			target.add(node);
		}
	}

	Node getElseNode() {
		if (target == null) {
			return EmptyNode.EMPTY;
		}
		return target.build();
	}

	@Override
	public String toString() {
		return target == null ? super.toString()
				: super.toString() + " ELSE: " + target;
	}

}
