package au.com.codeka.carrot.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Params;

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
		case VALUE:
			return new ValueTerm(token.getValue());
		case IDENTIFIER:
			return getAccessTerm(tokenizer, token.getIdentifier());
		case LEFT_PARENTHESIS:
			Term term = ExpressionParser.parse(tokenizer);
			tokenizer.consume(TokenType.RIGHT_PARENTHESIS);
			return term;
		// TODO: Arrays and objects?
		default:
			throw new CarrotException("");
		}
	}

	private static final Set<TokenType> ACCESS_TYPE =
			Sets.immutableEnumSet(TokenType.DOT, TokenType.LEFT_BRACKET, TokenType.LEFT_PARENTHESIS);

	private static Term getAccessTerm(Tokenizer tokenizer, Term accessTerm) throws CarrotException {
		Term result = accessTerm;
		while (ACCESS_TYPE.contains(tokenizer.peek())) {
			Token token = tokenizer.next();
			switch (token.getType()) {
			case DOT:
				result = new AccessTerm(result, new ValueTerm(tokenizer.parseIdentifier()));
				break;
			case LEFT_BRACKET:
				result = new AccessTerm(result, ExpressionParser.parse(tokenizer));
				tokenizer.consume(TokenType.RIGHT_BRACKET);
				break;
			case RIGHT_BRACKET:
				result = new FunctionTerm(result, parseParams(tokenizer));
				tokenizer.consume(TokenType.RIGHT_PARENTHESIS);
				break;
			default:
			}
		}
		return result;
	}

	private static List<Term> parseParams(Tokenizer tokenizer) throws CarrotException {
		if (tokenizer.peek() == TokenType.RIGHT_PARENTHESIS) {
			return Collections.emptyList();
		}
		List<Term> params = new ArrayList<>();
		do {
			params.add(tokenizer.parseExpression());
		} while (tokenizer.tryConsume(TokenType.COMMA));
		return params;
	}

}
