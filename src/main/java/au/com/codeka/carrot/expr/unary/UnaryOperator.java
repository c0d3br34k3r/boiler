package au.com.codeka.carrot.expr.unary;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.ValueHelper;

public enum UnaryOperator {

	NOT {
		@Override
		public Object apply(Object value) throws CarrotException {
			return !ValueHelper.isTrue(value);
		}
	},

	MINUS {
		@Override
		public Object apply(Object value) throws CarrotException {
			return ValueHelper.negate(value);
		}
	},

	PLUS {
		@Override
		public Object apply(Object value) throws CarrotException {
			return ValueHelper.toNumber(value);
		}
	};

	public abstract Object apply(Object value) throws CarrotException;

}
