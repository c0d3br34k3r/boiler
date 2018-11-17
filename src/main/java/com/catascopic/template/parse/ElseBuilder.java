package com.catascopic.template.parse;

abstract class ElseBuilder extends NodeBuilderTag {

	private BlockBuilder elseBuilder;

	@Override
	void setElse(NodeBuilderTag linked) {
		if (elseBuilder != null) {
			elseBuilder.setElse(linked);
		} else {
			elseBuilder = linked;
		}
	}

	@Override
	void add(Node node) {
		if (elseBuilder == null) {
			super.add(node);
		} else {
			elseBuilder.add(node);
		}
	}

	Node getElseNode() {
		if (elseBuilder == null) {
			return EmptyNode.EMPTY;
		}
		return elseBuilder.build();
	}

	@Override
	public String toString() {
		return elseBuilder == null ? super.toString()
				: super.toString() + ", elseBuilder=" + elseBuilder;
	}

}
