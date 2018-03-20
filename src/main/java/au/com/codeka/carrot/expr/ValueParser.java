package au.com.codeka.carrot.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import au.com.codeka.carrot.TemplateParseException;

class ValueParser implements TermParser {

	private static final Set<Symbol> ACCESS_TYPE =
			Sets.immutableEnumSet(Symbol.DOT, Symbol.LEFT_BRACKET, Symbol.LEFT_PARENTHESIS);

	@Override
	public Term parse(Tokenizer tokenizer) {
		Token token = tokenizer.next();
		Term term;
		switch (token.type()) {
		case VALUE:
			term = new ValueTerm(token.value());
			break;
		case IDENTIFIER:
			term = new Variable(token.identifier());
			break;
		case SYMBOL:
			switch (token.symbol()) {
			case PLUS:
			case MINUS:
			case NOT:
				return new UnaryTerm(token.symbol().unaryOperator(), parse(tokenizer));
			case LEFT_PARENTHESIS:
				term = ExpressionParser.parse(tokenizer);
				tokenizer.consume(Symbol.RIGHT_PARENTHESIS);
				break;
			case LEFT_BRACKET:
				term = parseList(tokenizer);
				break;
			case LEFT_CURLY_BRACKET:
				term = parseMap(tokenizer);
				break;
			default:
				throw new TemplateParseException("unexpected symbol %s", token.symbol());
			}
			break;
		default:
			throw new TemplateParseException("unexpected token %s", token);
		}
		for (;;) {
			Symbol symbol = tokenizer.tryConsume(ACCESS_TYPE);
			if (symbol == null) {
				return term;
			}
			switch (symbol) {
			case DOT:
				term = new IndexTerm(term, new ValueTerm(tokenizer.parseIdentifier()));
				break;
			case LEFT_BRACKET:
				term = new IndexTerm(term, ExpressionParser.parse(tokenizer));
				tokenizer.consume(Symbol.RIGHT_BRACKET);
				break;
			case LEFT_PARENTHESIS:
				throw new UnsupportedOperationException();
			default:
			}
		}
	}

	private static Term parseList(Tokenizer tokenizer) {
		if (tokenizer.tryConsume(Symbol.RIGHT_BRACKET)) {
			return ListTerm.EMPTY;
		}
		List<Term> terms = new ArrayList<>();
		do {
			terms.add(tokenizer.parseExpression());
		} while (tokenizer.tryConsume(Symbol.COMMA));
		tokenizer.consume(Symbol.RIGHT_BRACKET);
		return new ListTerm(terms);
	}

	private static Term parseMap(Tokenizer tokenizer) {
		if (tokenizer.tryConsume(Symbol.RIGHT_CURLY_BRACKET)) {
			return MapTerm.EMPTY;
		}
		Map<String, Term> terms = new HashMap<>();
		do {
			String key = parseKey(tokenizer);
			tokenizer.consume(Symbol.COLON);
			Term value = tokenizer.parseExpression();
			terms.put(key, value);
		} while (tokenizer.tryConsume(Symbol.COMMA));
		tokenizer.consume(Symbol.RIGHT_CURLY_BRACKET);
		return new MapTerm(terms);
	}

	private static String parseKey(Tokenizer tokenizer) {
		Token token = tokenizer.next();
		if (token.type() == TokenType.IDENTIFIER) {
			return token.identifier();
		}
		if (token.type() != TokenType.VALUE && !(token.value() instanceof String)) {
			throw new TemplateParseException("expected string key or identifier, got %s", token);
		}
		return (String) token.value();
	}

}
