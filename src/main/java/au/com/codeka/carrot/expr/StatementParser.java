package au.com.codeka.carrot.expr;

import static au.com.codeka.carrot.expr.TokenType.DIVIDE;
import static au.com.codeka.carrot.expr.TokenType.EOF;
import static au.com.codeka.carrot.expr.TokenType.EQUAL;
import static au.com.codeka.carrot.expr.TokenType.GREATER_THAN;
import static au.com.codeka.carrot.expr.TokenType.GREATER_THAN_OR_EQUAL;
import static au.com.codeka.carrot.expr.TokenType.IDENTIFIER;
import static au.com.codeka.carrot.expr.TokenType.IN;
import static au.com.codeka.carrot.expr.TokenType.LESS_THAN;
import static au.com.codeka.carrot.expr.TokenType.LESS_THAN_OR_EQUAL;
import static au.com.codeka.carrot.expr.TokenType.LOGICAL_AND;
import static au.com.codeka.carrot.expr.TokenType.LOGICAL_OR;
import static au.com.codeka.carrot.expr.TokenType.MINUS;
import static au.com.codeka.carrot.expr.TokenType.MULTIPLY;
import static au.com.codeka.carrot.expr.TokenType.NOT;
import static au.com.codeka.carrot.expr.TokenType.NOT_EQUAL;
import static au.com.codeka.carrot.expr.TokenType.PLUS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.expr.binary.BinaryTermParser;
import au.com.codeka.carrot.expr.unary.UnaryTermParser;
import au.com.codeka.carrot.expr.values.ErrorTermParser;
import au.com.codeka.carrot.expr.values.ValueParser;
import au.com.codeka.carrot.tag.Tag;
import au.com.codeka.carrot.tmpl.TagNode;

/**
 * StatementParser is used to parse expressions. Expressions are used to refer
 * to everything that appears after the {@link Tag} in a {@link TagNode}, and
 * has the following pseudo-EBNF grammar:
 *
 * <pre>
 * <code>
 *  value =
 *     variable
 *     | number
 *     | literal
 *     | "(" expression ")"
 *     | empty-term
 *
 *   unary-term = ["!"] unary-term
 *
 *   multiplicative-term = unnary-term [("*" | "/") multiplicative-term]
 *
 *   additive-term = multiplicative-term [("+" | "-") additive-term]
 *
 *   relational-term = additive-term [("&lt;" | &lt;=" | "&gt;" | &gt;=" | "in") relational-term]
 *
 *   equality-term = relational-term [("==" | "!=") equality-term]
 *
 *   and-term = equality-term ["&amp;&amp;" and-term]
 *
 *   or-term = and-term ["||" or-term]
 *
 *   emtpy-term =
 *
 *   expression = or-term ["," expression]
 *
 *   variable = identifier [func-call] ["[" expression "]"] ["." variable]
 *
 *   func-call = "." identifier "(" expression ")"
 *
 *   identifier = "any valid Java identifier"
 *   number = "and valid Java number"
 *   literal = """ anything """
 * </code>
 * </pre>
 *
 * <p>
 * The statement parser allows you to extract any sub-element from a string as
 * well. For example, the ForTag wants to pull off its arguments: an identifier,
 * followed by the identifier "in", followed by a statement.
 */
public class StatementParser {

	private final Tokenizer tokenizer;

	public StatementParser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	/**
	 * Parses the "end" of the statement. Just verifies that there's no
	 * unexpected tokens after the end.
	 *
	 * @throws CarrotException if we're not actually at the end of the
	 *         statement.
	 */
	public void parseEnd() throws CarrotException {
		tokenizer.get(EOF);
	}

	@Nonnull
	public Token get(@Nonnull TokenType type) throws CarrotException {
		return tokenizer.get(type);
	}

	public boolean tryConsume(TokenType type) throws CarrotException {
		return tokenizer.tryConsume(type);
	}

	public String parseIdentifier() throws CarrotException {
		return (String) tokenizer.get(IDENTIFIER).getValue();
	}

	@Nullable
	public String tryParseIdentifier() throws CarrotException {
		Token token = tokenizer.tryGet(IDENTIFIER);
		return token != null ? (String) token.getValue() : null;
	}

	public Term parseExpression() throws CarrotException {
		return EXPRESSION_PARSER.parse(tokenizer);
	}

	private static final TermParser EXPRESSION_PARSER = new TermParser() {

		private final TermParser parser =
		// @formatter:off
		new BinaryTermParser(
		  new BinaryTermParser(
		    new BinaryTermParser(
		      new BinaryTermParser(
		        new BinaryTermParser(
		          new BinaryTermParser(
		            new UnaryTermParser(
		              new ValueParser(ErrorTermParser.INSTANCE, this),
		              NOT, PLUS, MINUS),
		            MULTIPLY, DIVIDE),
		          PLUS, MINUS),
		        LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, IN),
		      EQUAL, NOT_EQUAL),
		    LOGICAL_AND),
		  LOGICAL_OR);
		// @formatter:on

		@Override
		public Term parse(Tokenizer tokenizer) throws CarrotException {
			return parser.parse(tokenizer);
		}
	};

}
