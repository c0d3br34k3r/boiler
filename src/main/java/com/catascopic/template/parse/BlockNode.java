package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

class BlockNode implements Node {

	private final Block block;

	BlockNode(Block block) {
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		block.render(writer, scope);
	}

}
