package expressions.types.operations;

import expressions.types.ArithmeticExpression;

import java.util.Map;

public class Add extends ArithmeticOperation {

    public Add(ArithmeticExpression left, ArithmeticExpression right) {
        super(left, right, (a, b) -> (a + b));
    }

    @Override
    protected ArithmeticExpression toPolynomialImpl() {
        if (equals(left, 0)) {
            return right;
        }
        if (equals(right, 0)) {
            return left;
        }
        return this;
    }

    @Override
    protected void processCoefficients(Map<Integer, Integer> coefficientByPow, int sign) {
        processAddOrSubtract(left, sign, coefficientByPow);
        processAddOrSubtract(right, sign, coefficientByPow);
    }

    @Override
    protected Character getSymbol() {
        return '+';
    }
}