package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

class BlockBuilder {
	
	private List<Node> nodes = new ArrayList<>();
	private boolean end; // = false

	void add(Node node) {
		nodes.add(node);
	}

	void end() {
		end = true;
	}

	void beginElse() {

	}

	public Block parseBlock() {
		new BlockBuilder();
	}

}
