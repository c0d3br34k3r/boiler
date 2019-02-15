package com.catascopic.template.expr;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import com.catascopic.template.TrackingReader;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.TemplateRenderException;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.TestUtil;
import com.google.common.collect.ImmutableMap;

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
			evaluate(expr);
			Assert.fail(expr + " was valid");
		} catch (TemplateParseException | TemplateRenderException e) {
			System.out.printf("%-24s %s%n", expr, toString(e));
		}
	}

	private static Object toString(Throwable throwable) {
		StringBuilder builder = new StringBuilder();
		Throwable t = throwable;
		for (;;) {
			builder.append(t.getMessage());
			t = t.getCause();
			if (t == null) {
				return builder.toString();
			}
			builder.append("; ");
		}
	}

	private static void evaluate(String expr) {
		Tokenizer tokenizer =
				new Tokenizer(TrackingReader.create(new StringReader(expr)));
		Term term;
		term = tokenizer.parseExpression();
		tokenizer.end();
		term.evaluate(
				TestUtil.testScope(ImmutableMap.of("word", "automobile")));
	}

}
