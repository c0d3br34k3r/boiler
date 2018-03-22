package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

class LastElseNode implements Node {

	private final Block block;

	public LastElseNode(Block block) {
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		block.renderContent(writer, scope);
	}

}
