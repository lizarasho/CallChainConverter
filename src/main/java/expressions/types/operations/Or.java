package expressions.types.operations;

import expressions.types.ArithmeticExpression;
import expressions.types.LogicalExpression;
import expressions.types.primitives.Bool;
import expressions.types.primitives.Const;

public class Or extends BoolOperation {

    public Or(LogicalExpression left, LogicalExpression right) {
        super(left, right, (a, b) -> a || b);
    }

    @Override
    protected LogicalExpression trySimplifyDistributive() {
        return null;
    }

    @Override
    protected LogicalExpression simplifyBoolean() {
        return commonSimplifyBoolean(false);
    }

    @Override
    protected LogicalExpression trySimplifyComparison(ComparisonOperation left, ComparisonOperation right) {
        ArithmeticExpression exp = left.getLeft();
        int a = ((Const) left.getRight()).getValue();
        int b = ((Const) right.getRight()).getValue();

        // (exp < a) | (exp < b) <=> exp < max(a, b)
        if (left instanceof Less && right instanceof Less) {
            return new Less(exp, new Const(Math.max(a, b)));
        }

        // (exp > a) & (exp > b) <=> (exp > min(a, b))
        if (left instanceof Greater && right instanceof Greater) {
            return new Greater(exp, new Const(Math.min(a, b)));
        }

        // (exp < a) | (exp > b) <=> true, if b < a
        if (left instanceof Less && right instanceof Greater && (b < a)) {
            return new Bool(true);
        }

        // (exp > a) | (exp < b) <=> true, if a < b
        if (left instanceof Greater && right instanceof Less && (a < b)) {
            return new Bool(true);
        }

        // (exp = a) | (exp = b) <=> (exp = a), if a == b
        if (left instanceof Equals && right instanceof Equals && (a == b)) {
            return left;
        }

        // (exp < a) | (exp = b) <=> (exp < a), if b < a
        if (left instanceof Less && right instanceof Equals && (b < a)) {
            return left;
        }

        // (exp = a) | (exp < b) <=> (exp < b), if a < b
        if (left instanceof Equals && right instanceof Less && (a < b)) {
            return right;
        }

        // (exp > a) | (exp = b) <=> (exp > a), if b > a
        if (left instanceof Greater && right instanceof Equals && (b > a)) {
            return left;
        }

        // (exp = a) & (exp > b) <=> (exp > b), if a > b
        if (left instanceof Equals && right instanceof Greater && (a > b)) {
            return right;
        }
        return this;
    }

    @Override
    protected Character getSymbol() {
        return '|';
    }

}
