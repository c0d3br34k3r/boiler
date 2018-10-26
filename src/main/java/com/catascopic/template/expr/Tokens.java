package com.catascopic.template.expr;

import com.catascopic.template.Null;
import com.catascopic.template.Values;

class Tokens {

	static final Token TRUE = new ValueToken(true);
	static final Token FALSE = new ValueToken(false);
	static final Token NULL = new ValueToken(Null.NULL);
	static final Token END = new AbstractToken(TokenType.END) {

		@Override
		public String toString() {
			return "END";
		}
	};

	static Token valueOf(Object value) {
		return new ValueToken(value);
	}

	static Token identifier(String value) {
		return new IdentifierToken(value);
	}

	private static abstract class AbstractToken implements Token {

		private final TokenType type;

		AbstractToken(TokenType type) {
			this.type = type;
		}

		@Override
		public TokenType type() {
			return type;
		}

		@Override
		public Object value() {
			throw new IllegalStateException(this + " is not a value");
		}

		@Override
		public String identifier() {
			throw new IllegalStateException(this + " is not an identifier");
		}

		@Override
		public Symbol symbol() {
			throw new IllegalStateException(this + " is not a symbol");
		}
	}

	private static class ValueToken extends AbstractToken {

		private final Object value;

		private ValueToken(Object value) {
			super(TokenType.VALUE);
			this.value = value;
		}

		@Override
		public Object value() {
			return value;
		}

		@Override
		public String toString() {
			return Values.toString(value);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof ValueToken
					&& ((ValueToken) obj).value == value;
		}

		@Override
		public int hashCode() {
			return TokenType.VALUE.hashCode() ^ value.hashCode();
		}
	}

	private static class IdentifierToken extends AbstractToken {

		private final String value;

		private IdentifierToken(String value) {
			super(TokenType.IDENTIFIER);
			this.value = value;
		}

		@Override
		public String identifier() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof IdentifierToken
					&& ((ValueToken) obj).value == value;
		}

		@Override
		public int hashCode() {
			return TokenType.IDENTIFIER.hashCode() ^ value.hashCode();
		}
	}

}
