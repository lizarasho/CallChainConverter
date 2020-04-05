package expressions.types.operations;

import expressions.types.ArithmeticExpression;

public class Greater extends ComparisonOperation {

    public Greater(ArithmeticExpression left, ArithmeticExpression right) {
        super(left, right, (a, b) -> a > b);
    }

    @Override
    protected Character getSymbol() {
        return '>';
    }
}