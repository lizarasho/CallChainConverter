package expressions.types.primitives;

import expressions.types.LogicalExpression;
import expressions.types.Expression;

public class Bool implements LogicalExpression {
    private boolean value;

    public Bool(boolean value) {
        this.value = value;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    public boolean isTrue() {
        return value;
    }

    @Override
    public String toString() {
        if (value) {
            return "(0=0)";
        }
        return "(1=0)";
    }
}
