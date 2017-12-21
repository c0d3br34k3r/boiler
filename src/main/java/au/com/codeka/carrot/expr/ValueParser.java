package au.com.codeka.carrot.expr;

import java.util.Set;

import com.google.common.collect.Sets;

import au.com.codeka.carrot.CarrotException;

class ValueParser implements TermParser {

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Token token = tokenizer.next();
		TokenType type = token.getType();
		switch (type) {
			case PLUS:
			case MINUS:
			case NOT:
				return new UnaryTerm(type.unaryOperator(), parse(tokenizer));
			case NUMBER_LITERAL:
			case STRING_LITERAL:
			case BOOLEAN_LITERAL:
				return new ValueTerm(token.getValue());
			case IDENTIFIER:
				return getAccessTerm(tokenizer, new Variable((String) token.getValue()));
			case LEFT_PARENTHESIS:
				Term term = ExpressionParser.parse(tokenizer);
				tokenizer.get(TokenType.RIGHT_PARENTHESIS);
				return term;
			// TODO: Arrays and objects?
			default:
				throw new CarrotException("");
		}
	}

	private static final Set<TokenType> ACCESS_TYPE =
			Sets.immutableEnumSet(TokenType.DOT, TokenType.LEFT_BRACKET);

	private static Term getAccessTerm(Tokenizer tokenizer, Term accessTerm) throws CarrotException {
		Term result = accessTerm;
		while (tokenizer.check(ACCESS_TYPE)) {
			Token token = tokenizer.next();
			switch (token.getType()) {
				case DOT:
					result = new AccessTerm(result,
							new ValueTerm(tokenizer.get(TokenType.IDENTIFIER).getValue()));
					break;
				case LEFT_BRACKET:
					result = new AccessTerm(result, ExpressionParser.parse(tokenizer));
					tokenizer.get(TokenType.RIGHT_BRACKET);
					break;
				default:
			}
		}
		return result;
	}

}
