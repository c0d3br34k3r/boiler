package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.ValueHelper;

enum UnaryOperator {

	NOT {
		@Override
		Object apply(Object value) throws CarrotException {
			return !ValueHelper.isTrue(value);
		}
	},

	MINUS {
		@Override
		Object apply(Object value) throws CarrotException {
			return ValueHelper.negate(value);
		}
	},

	PLUS {
		@Override
		Object apply(Object value) throws CarrotException {
			return ValueHelper.toNumber(value);
		}
	};

	abstract Object apply(Object value) throws CarrotException;

}
