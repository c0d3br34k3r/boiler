package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;
import com.catascopic.template.parse.Variables.Assigner;

class TemplateNode implements Node {

	private final Term templateName;
	private final Assigner assigner;

	TemplateNode(Term template, Assigner assigner) {
		this.templateName = template;
		this.assigner = assigner;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		scope.renderTemplate(writer, Values.toString(templateName.evaluate(
				scope)), assigner);
	}

}
