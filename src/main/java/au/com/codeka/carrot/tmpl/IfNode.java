package au.com.codeka.carrot.tmpl;

import java.io.IOException;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.Tokenizer;

public class IfNode {

	public static IfBuilder expression(Tokenizer tokenizer) throws CarrotException {
		Term expr = tokenizer.parseExpression();
		tokenizer.end();
		return new IfBuilder(expr);
	}

	public static class IfBuilder {

		private Term expr;
		private Node elseNode;

		private IfBuilder(Term expr) {
			this.expr = expr;
		}

		public Node childNodes(Parser parser) throws IOException, CarrotException {
			parser.next();
		}


	}

}
