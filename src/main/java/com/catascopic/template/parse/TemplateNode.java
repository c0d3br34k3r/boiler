package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Assigner;
import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;

class TemplateNode implements Node, Tag {

	private final Term templateName;
	private final Assigner assigner;

	TemplateNode(Term template, Assigner assigner) {
		this.templateName = template;
		this.assigner = assigner;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		scope.renderTemplate(writer,
				Values.toString(templateName.evaluate(scope)), assigner);
	}

	@Override
	public String toString() {
		return "<% template " + templateName + " %>";
	}

	static Tag parseTag(Tokenizer tokenizer) {
		Term templateName = tokenizer.parseExpression();
		Assigner vars;
		if (tokenizer.tryConsume("with")) {
			vars = Variables.parseAssignment(tokenizer);
		} else {
			vars = Variables.EMPTY;
		}
		return new TemplateNode(templateName, vars);
	}

	@Override
	public Node createNode(TagStream stream) {
		return this;
	}

}
