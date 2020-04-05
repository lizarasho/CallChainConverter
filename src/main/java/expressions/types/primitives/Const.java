package expressions.types.primitives;

import expressions.types.ArithmeticExpression;
import expressions.types.Expression;

public class Const implements ArithmeticExpression {
    private int value;

    public Const(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Const that = (Const) obj;
        return value == that.value;
    }
}
