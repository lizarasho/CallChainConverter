package expressions.types.operations;

import expressions.types.ArithmeticExpression;
import expressions.types.LogicalExpression;
import expressions.types.primitives.Bool;
import expressions.types.primitives.Const;
import expressions.types.primitives.Element;
import expressions.types.Expression;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Implementation of the {@code LogicalExpression} representing relational expressions,
 * i.e logical binary operations with arithmetic operands.
 */
public abstract class ComparisonOperation extends BinaryOperation implements LogicalExpression {

    protected ArithmeticExpression left, right;

    private final BiFunction<Integer, Integer, Boolean> action;

    ComparisonOperation(ArithmeticExpression left, ArithmeticExpression right, BiFunction<Integer, Integer, Boolean> action) {
        this.left = left;
        this.right = right;
        this.action = action;
    }

    @Override
    public LogicalExpression simplify() {
        ComparisonOperation result = simplifySummands();
        if (result.getLeft() instanceof Const && result.getRight() instanceof Const) {
            return new Bool(action.apply(((Const) result.getLeft()).getValue(), ((Const) result.getRight()).getValue()));
        }
        return result;
    }

    /**
     * Simplifies both of operands and constructs the equivalent expression in simplified form
     * where left part depends on variable {@link Element} and right part is a constant {@link Const}
     *
     * @return {@code ComparisonOperation} in simplified form
     */
    private ComparisonOperation simplifySummands() {
        ArithmeticExpression leftPart = (new Subtract(left, right)).simplify();
        ArithmeticExpression rightPart = new Const(0);
        if (leftPart instanceof ArithmeticOperation) {
            Map<Integer, Integer> coefficientsByPow = ((ArithmeticOperation) leftPart).calcSummands();
            int k = 0;
            if (coefficientsByPow != null && coefficientsByPow.containsKey(0)) {
                k = coefficientsByPow.get(0);
            }
            leftPart = (new Subtract(leftPart, new Const(k))).simplify();
            rightPart = new Const(-k);
        }
        try {
            return getClass().getDeclaredConstructor(ArithmeticExpression.class, ArithmeticExpression.class)
                    .newInstance(leftPart, rightPart);
        } catch (Exception ignored) {
        }
        return this;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + getSymbol() + right.toString() + ")";
    }

    protected abstract Character getSymbol();

    public ArithmeticExpression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = (ArithmeticExpression) right;
    }

    public ArithmeticExpression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = (ArithmeticExpression) left;
    }
}
