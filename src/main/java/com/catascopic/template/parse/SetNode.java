package com.catascopic.template.parse;

import com.catascopic.template.Assigner;
import com.catascopic.template.Scope;
import com.catascopic.template.eval.Tokenizer;

class SetNode implements Node, Tag {

	private final Assigner assigner;

	SetNode(Assigner assigner) {
		this.assigner = assigner;
	}

	@Override
	public void render(Appendable writer, Scope scope) {
		assigner.assign(scope);
	}

	@Override
	public String toString() {
		return "<% set " + assigner + " %>";
	}

	public static Tag parseTag(Tokenizer tokenizer) {
		return new SetNode(Variables.parseAssignment(tokenizer));
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

}
