package com.catascopic.template.zero;

public abstract class AbstractValue implements Value {

	@Override
	public Value add(Value value) {
		throw new IllegalStateException();
	}

	@Override
	public Value multiply(Value value) {
		throw new IllegalStateException();
	}

	@Override
	public Value divide(Value value) {
		throw new IllegalStateException();
	}

	@Override
	public Value modulo(Value value) {
		throw new IllegalStateException();
	}

	@Override
	public Value negate() {
		throw new IllegalStateException();
	}

	@Override
	public Value index(Value index) {
		throw new IllegalStateException();
	}

	@Override
	public Value access(Value key) {
		throw new IllegalStateException();
	}

	@Override
	public Value size() {
		throw new IllegalStateException();
	}

	@Override
	public Value and(Value other) {
		throw new IllegalStateException();
	}

	@Override
	public Value or(Value other) {
		throw new IllegalStateException();
	}

	@Override
	public Value not() {
		throw new IllegalStateException();
	}

	@Override
	public int compare(Value other) {
		throw new IllegalStateException();
	}

	@Override
	public boolean equals(Value other) {
		throw new IllegalStateException();
	}

}
