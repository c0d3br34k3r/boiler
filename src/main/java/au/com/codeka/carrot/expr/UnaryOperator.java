package au.com.codeka.carrot.expr;

enum UnaryOperator {

	NOT {
		@Override
		Object apply(Object value) {
			return !Values.isTrue(value);
		}
	},
	NEGATIVE {
		@Override
		Object apply(Object value) {
			return Values.negate(value);
		}
	},
	POSITIVE {
		@Override
		Object apply(Object value) {
			return Values.toNumber(value);
		}
	};

	abstract Object apply(Object value);

}
