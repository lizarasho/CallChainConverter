package expressions.types.operations;

import expressions.types.ArithmeticExpression;
import expressions.types.primitives.Const;
import expressions.types.primitives.Element;
import expressions.types.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Implementation of {@code ArithmeticExpression} representing binary operation with arithmetic type.
 */
public abstract class ArithmeticOperation extends BinaryOperation implements ArithmeticExpression {

    protected ArithmeticExpression left, right;

    private final BiFunction<Integer, Integer, Integer> action;

    ArithmeticOperation(ArithmeticExpression left, ArithmeticExpression right,
                        BiFunction<Integer, Integer, Integer> action) {
        this.left = left;
        this.right = right;
        this.action = action;
    }

    @Override
    public ArithmeticExpression simplify() {
        ArithmeticExpression result = toPolynomial();
        if (result instanceof ArithmeticOperation) {
            result = convertToExpression(((ArithmeticOperation) result).calcSummands());
        }
        return result;
    }

    /**
     * Simplifies by some simple rules and expands distributive laws, if it is possible.
     *
     * @return {@code ArithmeticExpression} representing expression simplified to polynomial
     */
    ArithmeticExpression toPolynomial() {
        if (left instanceof ArithmeticOperation) {
            left = ((ArithmeticOperation) left).toPolynomial();
        }
        if (right instanceof ArithmeticOperation) {
            right = ((ArithmeticOperation) right).toPolynomial();
        }
        if (left instanceof Const && right instanceof Const) {
            return new Const(action.apply(((Const) left).getValue(), ((Const) right).getValue()));
        }
        return toPolynomialImpl();
    }

    /**
     * Simplifies by some simple rules and implements expanding brackets using distributive rule, if it is possible,
     * for specified arithmetic operation.
     *
     * @return simplified equivalent expression.
     */
    abstract ArithmeticExpression toPolynomialImpl();

    /**
     * Recursively visits all nodes of expressions binary tree and calculates summary coefficient for every summand.
     *
     * @return a {@code Map} containing summary coefficient for every element in specified pow.
     */
    Map<Integer, Integer> calcSummands() {
        Map<Integer, Integer> coefficientByPow = new HashMap<>();
        processCoefficients(coefficientByPow, 1);
        return coefficientByPow.entrySet().stream().filter(e -> e.getValue() != 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    void processAddOrSubtract(ArithmeticExpression expression, int sign, Map<Integer, Integer> coefficientByPow) {
        if (expression instanceof Element) {
            updateCoefficientsMap(((Element) expression).getPow(), sign, coefficientByPow);
            return;
        }
        if (expression instanceof Const) {
            updateCoefficientsMap(0, sign * ((Const) expression).getValue(), coefficientByPow);
            return;
        }
        if (expression != null) {
            ((ArithmeticOperation) expression).processCoefficients(coefficientByPow, sign);
        }
    }

    protected abstract void processCoefficients(Map<Integer, Integer> coefficientByPow, int sign);

    /**
     * Generates representation of {@code ArithmeticExpression} based on the provided map {@code coefficientByPow}.
     *
     * @param coefficientByPow {@code Map} representing summary coefficients for every element in specified pow
     * @return {@code ArithmeticExpression} based on {@code coefficientByPow}.
     */
    private ArithmeticExpression convertToExpression(Map<Integer, Integer> coefficientByPow) {
        ArithmeticExpression result = null;
        boolean first = true;

        for (Map.Entry<Integer, Integer> entry : coefficientByPow.entrySet()) {
            int pow = entry.getKey();
            int coefficient = entry.getValue();
            ArithmeticExpression expression;

            int resultCoefficient = first ? coefficient : Math.abs(coefficient);
            if (pow == 0) {
                expression = new Const(resultCoefficient);
            } else if (resultCoefficient == 1) {
                expression = new Element(pow);
            } else {
                expression = new Multiply(new Const(resultCoefficient), new Element(pow));
            }
            if (first) {
                result = expression;
                first = false;
                continue;
            }
            if (coefficient > 0) {
                result = new Add(result, expression);
            } else {
                result = new Subtract(result, expression);
            }
        }
        if (result == null) {
            return new Const(0);
        }
        return result;
    }

    void updateCoefficientsMap(int pow, int coefficient, Map<Integer, Integer> coefficientByPow) {
        if (coefficientByPow.containsKey(pow)) {
            coefficient += coefficientByPow.get(pow);
        }
        coefficientByPow.put(pow, coefficient);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + getSymbol() + right.toString() + ")";
    }

    protected abstract Character getSymbol();


    protected boolean equals(ArithmeticExpression expression, int n) {
        return expression instanceof Const && ((Const) expression).getValue() == n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArithmeticOperation that = (ArithmeticOperation) o;

        Map<Integer, Integer> thisCoefficients = this.calcSummands();
        Map<Integer, Integer> thatCoefficients = that.calcSummands();

        if (thisCoefficients.size() != thatCoefficients.size()) {
            return false;
        }
        for (Map.Entry<Integer, Integer> entry : thisCoefficients.entrySet()) {
            int k = entry.getKey();
            int v = entry.getValue();
            if (!thatCoefficients.containsKey(k) || thatCoefficients.get(k) != v) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ArithmeticExpression getLeft() {
        return left;
    }

    @Override
    public ArithmeticExpression getRight() {
        return right;
    }

    @Override
    public void setLeft(Expression left) {
        this.left = (ArithmeticExpression) left;
    }

    @Override
    public void setRight(Expression right) {
        this.right = (ArithmeticExpression) right;
    }
}
