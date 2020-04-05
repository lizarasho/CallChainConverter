package expressions.types.operations;

import expressions.types.ArithmeticExpression;
import expressions.types.LogicalExpression;
import expressions.types.primitives.Bool;
import expressions.types.primitives.Const;
import expressions.types.Expression;

import java.util.function.BiFunction;

/**
 * Implementation of the {@link LogicalExpression} representing binary operations with logical operands.
 */
public abstract class BoolOperation extends BinaryOperation implements LogicalExpression {

    private final BiFunction<Boolean, Boolean, Boolean> action;

    LogicalExpression left, right;

    BoolOperation(LogicalExpression left, LogicalExpression right, BiFunction<Boolean, Boolean, Boolean> action) {
        this.left = left;
        this.right = right;
        this.action = action;
    }

    @Override
    public LogicalExpression simplify() {
        left = (LogicalExpression) left.simplify();
        right = (LogicalExpression) right.simplify();

        if (left instanceof Bool && right instanceof Bool) {
            return new Bool(action.apply(((Bool) left).isTrue(), ((Bool) right).isTrue()));
        }

        LogicalExpression result;
        result = simplifyBoolean();
        if (result != null) {
            return result;
        }

        result = trySimplifyDistributive();
        if (result != null) {
            return result;
        }

        if (!(left instanceof ComparisonOperation) || !(right instanceof ComparisonOperation)) {
            return this;
        }
        ComparisonOperation comparisonLeft = (ComparisonOperation) left;
        ComparisonOperation comparisonRight = (ComparisonOperation) right;
        if (!(comparisonLeft.getRight() instanceof Const || !(comparisonRight.getRight() instanceof Const))) {
            return this;
        }
        ArithmeticExpression exp = comparisonLeft.getLeft();
        if (!exp.equals(comparisonRight.getLeft())) {
            return this;
        }

        result = trySimplifyComparison(comparisonLeft, comparisonRight);

        return result == null ? this : result;
    }

    /**
     * Tries to expand distributive laws.
     *
     * @return {@link LogicalExpression} simplified expression if the simplification is possible.
     */
    protected abstract LogicalExpression trySimplifyDistributive();

    /**
     * Simplifies logic expression if one of operands is {@link Bool}.
     *
     * @return {@link LogicalExpression} simplified equivalent expression.
     */
    protected abstract LogicalExpression simplifyBoolean();

    /**
     * Tries to simplify logic expression if both operands are comparison expressions with the same expression in the left part.
     *
     * @return {@link LogicalExpression} simplified equivalent expression if the simplification is possible.
     */
    protected abstract LogicalExpression trySimplifyComparison(ComparisonOperation left, ComparisonOperation right);

    @Override
    public String toString() {
        return "(" + left.toString() + getSymbol() + right.toString() + ")";
    }

    protected abstract Character getSymbol();

    LogicalExpression commonSimplifyBoolean(boolean invert) {
        LogicalExpression returnIfLeft = invert ? right : left;
        LogicalExpression returnIfRight = invert ? left : right;
        if (left instanceof Bool) {
            return ((Bool) left).isTrue() ? returnIfLeft : returnIfRight;
        }
        if (right instanceof Bool) {
            return ((Bool) right).isTrue() ? returnIfRight : returnIfLeft;
        }
        return null;
    }

    public LogicalExpression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = (LogicalExpression) right;
    }

    public LogicalExpression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = (LogicalExpression) left;
    }
}
