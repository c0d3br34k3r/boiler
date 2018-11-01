package com.catascopic.template.x;

public class IntValue implements Value {

	private final int value;

	public IntValue(int value) {
		this.value = value;
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public Value add(Value other) {
		return new IntValue(value + other.intValue());
	}

	@Override
	public Value multiply(Value other) {
		return new IntValue(value * other.intValue());
	}

	@Override
	public Value divide(Value other) {
		return new IntValue(value / other.intValue());
	}

	@Override
	public Value modulo(Value other) {
		return new IntValue(value % other.intValue());
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
