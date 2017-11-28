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
		this.valueParser = new IdentifierTermParser(EmptyTermParser.INSTANCE);
		this.iterationTerm = iterationTerm;
		this.expressionTerm = expressionTerm;
		this.identifierTerm = identifierTerm;
	}

	private static final Set<TokenType> ACCESS_TYPE =
			Sets.immutableEnumSet(TokenType.DOT, TokenType.LEFT_BRACKET,
					TokenType.LEFT_PARENTHESIS);

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Term left = valueParser.parse(tokenizer);
		if (left == EmptyTerm.INSTANCE) {
			return left;
		}
		AccessibleTerm result = new Unaccessible(new Variable(left));
		for (;;) {
			Token token = tokenizer.expect(ACCESS_TYPE);
			if (token == null) {
				break;
			}
			switch (token.getType()) {
				case DOT:
					result = new AccessTerm(result, identifierTerm.parse(tokenizer), TokenType.DOT);
					break;
				case LEFT_BRACKET:
					// the accessor in [] is supposed to be any expression
					result = new AccessTerm(result, expressionTerm.parse(tokenizer),
							TokenType.LEFT_BRACKET);
					break;
				case LEFT_PARENTHESIS:
					// the accessor in () is supposed to be an iteration
					result = new Unaccessible(
							new MethodTerm(result, iterationTerm.parse(tokenizer)));
					break;
				default:
			}
			if (token.getType().closingType() != null) {
				tokenizer.require(token.getType().closingType());
			}
		}
		return result;
	}

}
