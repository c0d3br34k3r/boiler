package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;

class TemplateNode implements Node {

	private final Term templateName;
	private final Assignment vars;

	TemplateNode(Term template, Assignment vars) {
		this.templateName = template;
		this.vars = vars;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		scope.renderTemplate(writer, Values.toString(templateName.evaluate(scope)), vars);
	}

}
