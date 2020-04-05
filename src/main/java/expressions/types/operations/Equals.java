package expressions.types.operations;

import expressions.types.ArithmeticExpression;

public class Equals extends ComparisonOperation {

    public Equals(ArithmeticExpression left, ArithmeticExpression right) {
        super(left, right, Integer::equals);
    }

    @Override
    protected Character getSymbol() {
        return '=';
    }
}