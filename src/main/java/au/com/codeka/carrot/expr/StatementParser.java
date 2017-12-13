package au.com.codeka.carrot.expr;

import static au.com.codeka.carrot.expr.TokenType.ASSIGNMENT;
import static au.com.codeka.carrot.expr.TokenType.COMMA;
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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.expr.accessible.AccessTermParser;
import au.com.codeka.carrot.expr.binary.BinaryTermParser;
import au.com.codeka.carrot.expr.unary.UnaryTermParser;
import au.com.codeka.carrot.expr.values.ErrorTermParser;
import au.com.codeka.carrot.expr.values.IdentifierTermParser;
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

	/**
	 * Tries to parse an identifier from the stream and returns it if we parsed
	 * it, otherwise returns null.
	 *
	 * @return The {@link Identifier} we parsed, or null if we couldn't parse an
	 *         identifier.
	 * @throws CarrotException if there's some error parsing the identifer.
	 */
	@Nullable
	public Identifier tryParseIdentifier() throws CarrotException {
		Token token = tokenizer.tryGet(IDENTIFIER);
		return token != null ? new Identifier((String) token.getValue()) : null;
	}

	@Nonnull
	public Token parseToken(@Nonnull TokenType type) throws CarrotException {
		return tokenizer.get(type);
	}

	/**
	 * Attempts to parse an identifier list. If there's no identifier to begin
	 * the list, returns null.
	 *
	 * @return The list of {@link Identifier}s we parsed, or null if we couldn't
	 *         parse an identifier.
	 * @throws CarrotException if there's some error parsing the identifiers.
	 */
	@Deprecated
	public List<Identifier> tryParseIdentifierList() throws CarrotException {
		if (tokenizer.check(IDENTIFIER)) {
			return parseIdentifierList();
		}
		return null;
	}

	@Deprecated
	public List<Identifier> parseIdentifierList() throws CarrotException {
		// TODO: most efficient?
		List<Identifier> result = new LinkedList<>();
		// first token of a list is always an identifier
		do {
			result.add(new Identifier((String) tokenizer.get(IDENTIFIER).getValue()));
		} while (tokenizer.tryConsume(COMMA));
		return result;
	}

	public boolean isAssignment() throws CarrotException {
		return tokenizer.tryConsume(ASSIGNMENT);
	}

	public Term parseTerm() throws CarrotException {
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
		              new ValueParser(
		                new AccessTermParser(
		                  this,
		                  new IdentifierTermParser(ErrorTermParser.INSTANCE)), 
		                this),
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
