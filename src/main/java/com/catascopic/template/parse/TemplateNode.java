package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Assigner;
import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;

class TemplateNode implements Node {

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

}
