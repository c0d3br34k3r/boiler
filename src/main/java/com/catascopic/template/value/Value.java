package com.catascopic.template.value;

public interface Value {

	Value add(Value value);

	Value multiply(Value value);

	Value divide(Value value);

	Value modulo(Value value);

	Value negate();

	Value index(Value index);

	Value access(Value key);

	Value size();

	Value and(Value other);

	Value or(Value other);

	Value not();

	int compare(Value other);

	boolean equals(Value other);

	int intValue();

}
