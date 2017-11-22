package au.com.codeka.carrot.expr.accessible;

import java.util.Set;

import com.google.common.collect.Sets;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.expr.EmptyTerm;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TermParser;
import au.com.codeka.carrot.expr.Token;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;
import au.com.codeka.carrot.expr.binary.BinaryOperator;
import au.com.codeka.carrot.expr.binary.BinaryOperators;
import au.com.codeka.carrot.expr.values.EmptyTermParser;
import au.com.codeka.carrot.expr.values.IdentifierTermParser;
import au.com.codeka.carrot.expr.values.Variable;

/**
 * A parser for access {@link Term}s.
 *
 * @author Marten Gajda
 */
public final class AccessTermParser implements TermParser {
	private final TermParser valueParser;
	private final TermParser expressionTerm;
	private final TermParser identifierTerm;
	private final TermParser iterationTerm;

	public AccessTermParser(TermParser expressionTerm, TermParser identifierTerm,
			TermParser iterationTerm) {
		this.valueParser = new IdentifierTermParser(new EmptyTermParser());
		this.iterationTerm = iterationTerm;
		this.expressionTerm = expressionTerm;
		this.identifierTerm = identifierTerm;
	}

	private static final Set<TokenType> ACCESS_TYPE =
			Sets.immutableEnumSet(TokenType.DOT, TokenType.LSQUARE, TokenType.LPAREN);

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Term left = valueParser.parse(tokenizer);
		if (left instanceof EmptyTerm) {
			return left;
		}
		AccessibleTerm result = new Unaccessible(new Variable(left));

		while (tokenizer.accept(ACCESS_TYPE)) {
			Token token = tokenizer.expect(ACCESS_TYPE);
			switch (token.getType()) {
				case DOT:
					result = new AccessTerm(result, identifierTerm.parse(tokenizer), TokenType.DOT);
					break;
				case LSQUARE:
					// the accessor in [] is supposed to be any expression
					result = new AccessTerm(result, expressionTerm.parse(tokenizer),
							TokenType.LSQUARE);
					break;
				case LPAREN:
					// the accessor in () is supposed to be an iteration
					result = new Unaccessible(
							new MethodTerm(result, iterationTerm.parse(tokenizer)));
					break;
				default:
			}
			if (token.getType().closingType() != null) {
				tokenizer.expect(token.getType().closingType());
			}
		}
		return result;
	}
}
