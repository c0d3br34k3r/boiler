package com.catascopic.template.parse;

import com.catascopic.template.Assigner;
import com.catascopic.template.Location;
import com.catascopic.template.Scope;
import com.catascopic.template.eval.Tokenizer;

class SetNode implements Node {

	private final Assigner assigner;

	private SetNode(Assigner assigner) {
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

	static Tag parseTag(Tokenizer tokenizer) {
		Location location = tokenizer.getLocation();
		final Assigner assigner = Variables.parseAssignment(tokenizer);
		return new Tag(location) {

			@Override
			void handle(TemplateParser parser) {
				parser.add(new SetNode(assigner));
			}
		};
	}

}
