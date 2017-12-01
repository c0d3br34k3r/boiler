package au.com.codeka.carrot.expr;

import java.util.EnumMap;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A {@link Token} is something pulled off the statement stream and represents a
 * terminal like a string literal, number, identifier, etc.
 */
public class Token {

	private final TokenType tokenType;
	private final @Nullable Object value;

	private static final ImmutableMap<TokenType, Token> CACHE;

	static {
		EnumMap<TokenType, Token> map = new EnumMap<>(TokenType.class);
		for (TokenType type : TokenType.values()) {
			if (!type.hasValue()) {
				map.put(type, new Token(type));
			}
		}
		CACHE = Maps.immutableEnumMap(map);
	}

	private Token(TokenType tokenType) {
		this.tokenType = tokenType;
		this.value = null;
	}

	public static Token of(TokenType type) {
		return Preconditions.checkNotNull(CACHE.get(type));
	}

	public Token(TokenType type, String value) {
		this(type, (Object) value);
	}

	public Token(TokenType type, Number value) {
		this(type, (Object) value);
	}

	private Token(TokenType type, Object value) {
		this.tokenType = Preconditions.checkNotNull(type);
		this.value = Preconditions.checkNotNull(value);
	}

	public TokenType getType() {
		return tokenType;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		String str = tokenType.toString();
		if (tokenType.hasValue()) {
			str += " <" + value + ">";
		}
		return str;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Token)) {
			return false;
		}
		Token otherToken = (Token) other;
		return otherToken.tokenType == tokenType && Objects.equals(otherToken.value, value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tokenType, value);
	}

}
