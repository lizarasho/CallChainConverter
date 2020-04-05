package expressions.types.operations;

import expressions.types.ArithmeticExpression;

public class Less extends ComparisonOperation {

    public Less(ArithmeticExpression left, ArithmeticExpression right) {
        super(left, right, (a, b) -> a < b);
    }

    @Override
    protected Character getSymbol() {
        return '<';
    }
}