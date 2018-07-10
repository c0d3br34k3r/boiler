package com.catascopic.template.parse;

import java.io.IOException;
import java.util.List;

import com.catascopic.template.Scope;

abstract class Block {

	void render(Appendable writer, Scope scope) throws IOException {}

	void renderElse(Appendable writer, Scope scope) throws IOException {}

	static Block of(List<Node> nodes) {
		return of(nodes, EmptyNode.INSTANCE);
	}

	static Block of(final List<Node> nodes, final Node alternative) {
		return new Block() {

			@Override
			void render(Appendable writer, Scope scope) throws IOException {
				for (Node node : nodes) {
					node.render(writer, scope);
				}
			}

			@Override
			void renderElse(Appendable writer, Scope scope) throws IOException {
				alternative.render(writer, scope);
			}
		};
	}

}
