package expressions.types.operations;

import expressions.types.ArithmeticExpression;
import expressions.types.LogicalExpression;
import expressions.types.primitives.Bool;
import expressions.types.primitives.Const;

public class And extends BoolOperation {

    public And(LogicalExpression left, LogicalExpression right) {
        super(left, right, (a, b) -> a && b);
    }

    @Override
    protected LogicalExpression trySimplifyDistributive() {
        LogicalExpression leftSimpl = checkDistrByOperation(true);
        LogicalExpression rightSimpl = checkDistrByOperation(false);
        return leftSimpl != null ? leftSimpl : rightSimpl;
    }

    private LogicalExpression checkDistrByOperation(boolean leftDistributive) {
        LogicalExpression first = leftDistributive ? right : left;
        LogicalExpression second = leftDistributive ? left : right;
        if (second instanceof Or) {
            return (new Or(
                    new And(first, ((Or) second).getLeft()),
                    new And(first, ((Or) second).getRight())
            )).simplify();
        }
        return null;
    }

    @Override
    protected LogicalExpression simplifyBoolean() {
        return commonSimplifyBoolean(true);
    }


    @Override
    protected LogicalExpression trySimplifyComparison(ComparisonOperation left, ComparisonOperation right) {
        ArithmeticExpression exp = left.getLeft();
        int a = ((Const) left.getRight()).getValue();
        int b = ((Const) right.getRight()).getValue();

        // (exp < a) & (exp < b) <=> exp < min(a, b)
        if (left instanceof Less && right instanceof Less) {
            return new Less(exp, new Const(Math.min(a, b)));
        }

        // (exp > a) & (exp > b) <=> (exp > max(a, b))
        if (left instanceof Greater && right instanceof Greater) {
            return new Greater(exp, new Const(Math.max(a, b)));
        }

        // (exp < a) & (exp > b) <=> b < exp < a = false if b >= a
        if (left instanceof Less && right instanceof Greater && (b >= a)) {
            return new Bool(false);
        }

        // (exp > a) & (exp < b) <=> a < exp < b <=> false, if a >= b
        if (left instanceof Greater && right instanceof Less && (a >= b)) {
            return new Bool(false);
        }

        // (exp = a) & (exp = b) <=> (exp = a), if a == b
        // (exp = a) & (exp = b) <=> false, else
        if (left instanceof Equals && right instanceof Equals) {
            return a == b ? left : new Bool(false);
        }

        // (exp < a) & (exp = b) <=> (exp = b), if b < a
        // (exp < a) & (exp = b) <=> false, else
        if (left instanceof Less && right instanceof Equals) {
            return a > b ? right : new Bool(false);
        }

        // (exp = a) & (exp < b) <=> (exp = a), if a < b
        // (exp = a) & (exp < b) <=> false, else
        if (left instanceof Equals && right instanceof Less) {
            return b > a ? left : new Bool(false);
        }

        // (exp > a) & (exp = b) <=> (exp = b), if b > a
        // (exp > a) & (exp = b) <=> false, else
        if (left instanceof Greater && right instanceof Equals) {
            return b > a ? right : new Bool(false);
        }

        // (exp = a) & (exp > b) <=> (exp = a), if a > b
        // (exp = a) & (exp > b) <=> false, else
        if (left instanceof Equals && right instanceof Greater) {
            return a > b ? left : new Bool(false);
        }

        return null;
    }

    @Override
    protected Character getSymbol() {
        return '&';
    }
}
