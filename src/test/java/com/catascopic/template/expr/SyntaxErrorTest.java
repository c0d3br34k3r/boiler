package com.catascopic.template.expr;

import org.junit.Assert;
import org.junit.Test;

import com.catascopic.template.TemplateEvalException;
import com.catascopic.template.TemplateParseException;

public class SyntaxErrorTest {

	@Test
	public void testSyntaxError() {
		syntaxError("");
		syntaxError("(1)[0]");
		syntaxError("(1)[::-1]");
		syntaxError("'abc'[:::]");
		syntaxError("'abc'[::");
		syntaxError("'abc'[1 2]");
		syntaxError("'abc'[1 2 3]");
		syntaxError("'abc'[1:2 3]");
		syntaxError("'abc'[1 2:3]");
		syntaxError("'abc'[1:2:3:]");
		syntaxError("'abc'[1:2:3:4]");
		syntaxError("1 +");
		syntaxError("1 1");
		syntaxError("1 &! 1");
		syntaxError("max('a', 'b')");
		syntaxError("len(17)");
		syntaxError("word[:]");
		syntaxError("word[::]");
		syntaxError("word[4::]");
		syntaxError("word[:4:]");
		syntaxError("word[1:5:]");
		syntaxError("word[::0]");
		syntaxError("word[10]");
		syntaxError("word[-11]");
	}

	private static void syntaxError(String expr) {
		try {
			evaluate(expr, false);
			Assert.fail(expr + " was valid");
		} catch (TemplateParseException | TemplateEvalException e) {
			System.out.printf("%-24s %s%n", expr, e.getMessage());
		}
	}
	
}
