package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Location;
import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;

class EvalNode implements Node {

	private final Term expression;

	private EvalNode(Term expression) {
		this.expression = expression;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(Values.toString(expression.evaluate(scope)));
	}

	@Override
	public String toString() {
		return "<<" + expression + ">>";
	}

	static Tag getTag(Tokenizer tokenizer) {
		Location location = tokenizer.getLocation();
		final Term expression = tokenizer.parseExpression();
		return new Tag(location) {

			@Override
			void handle(TemplateParser parser) {
				parser.add(new EvalNode(expression));
			}
		};
	}

}
