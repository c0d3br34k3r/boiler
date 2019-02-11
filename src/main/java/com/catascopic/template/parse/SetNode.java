package com.catascopic.template.parse;

import com.catascopic.template.Assigner;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Tokenizer;

class SetNode implements Node, Tag {

	private final Assigner assigner;

	private SetNode(Assigner assigner) {
		this.assigner = assigner;
	}

	@Override
	public void render(Appendable writer, Scope scope) {
		assigner.assign(scope);
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

	static Tag parseTag(Tokenizer tokenizer) {
		return new SetNode(Variables.parseAssignment(tokenizer));
	}

	@Override
	public String toString() {
		return "set[" + assigner + "]";
	}

}
