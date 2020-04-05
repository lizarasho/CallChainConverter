package expressions.types.operations;

import expressions.types.ArithmeticExpression;
import expressions.types.primitives.Const;
import expressions.types.primitives.Element;

import java.util.Map;

public class Multiply extends ArithmeticOperation {

    public Multiply(ArithmeticExpression left, ArithmeticExpression right) {
        super(left, right, (a, b) -> a * b);
    }

    @Override
    ArithmeticExpression toPolynomialImpl() {
        if (right instanceof Const && ((Const) right).getValue() == 1) {
            return left;
        }
        if (left instanceof Const && ((Const) left).getValue() == 1) {
            return right;
        }

        ArithmeticExpression simpl = checkDistributive(new Class<?>[]{Add.class, Subtract.class});
        if (simpl != null) {
            return simpl;
        }

        if (left instanceof Element && right instanceof Element) {
            return new Element(((Element) left).getPow() + ((Element) right).getPow());
        }

        // (a*(b*c)) -> ((a*b)*c) where a.class == b.class
        // (a*(b*c)) -> ((a*c)*b) where a.class == c.class
        ArithmeticExpression leftSimpl = swapMultipliers(true);
        if (leftSimpl != null) {
            return leftSimpl;
        }

        // ((a*b)*c) -> ((a*c)*b) where a.class == c.class
        // ((a*b)*c) -> ((b*c)*a) where b.class == c.class
        ArithmeticExpression rightSimpl = swapMultipliers(false);
        if (rightSimpl != null) {
            return rightSimpl;
        }
        return this;
    }

    private ArithmeticExpression swapMultipliers(boolean leftAssociativity) {
        ArithmeticExpression first = leftAssociativity ? right : left;
        ArithmeticExpression second = leftAssociativity ? left : right;
        if (second instanceof Multiply) {
            if (((Multiply) second).getLeft().getClass() == first.getClass()) {
                return new Multiply(new Multiply(((Multiply) second).getLeft(), first), ((Multiply) second).getRight()).toPolynomial();
            } else {
                return new Multiply(new Multiply(((Multiply) second).getRight(), first), ((Multiply) second).getLeft()).toPolynomial();
            }
        }
        return null;
    }

    @Override
    protected void processCoefficients(Map<Integer, Integer> coefficientByPow, int sign) {
        if (left instanceof Const && right instanceof Element) {
            updateCoefficientsMap(((Element) right).getPow(), sign * ((Const) left).getValue(), coefficientByPow);
        }
        if (right instanceof Const && left instanceof Element) {
            updateCoefficientsMap(((Element) left).getPow(), sign * ((Const) right).getValue(), coefficientByPow);
        }
    }

    private ArithmeticExpression checkDistributive(Class<?>[] classes) {
        for (Class<?> clazz : classes) {
            ArithmeticExpression leftSimpl = checkDistrByOperation(clazz, true);
            if (leftSimpl != null) {
                return leftSimpl;
            }
            ArithmeticExpression rightSimpl = checkDistrByOperation(clazz, false);
            if (rightSimpl != null) {
                return rightSimpl;
            }
        }
        return null;
    }

    private ArithmeticExpression checkDistrByOperation(Class<?> clazz, boolean leftDistributive) {
        ArithmeticExpression first = leftDistributive ? right : left;
        ArithmeticExpression second = leftDistributive ? left : right;
        if (second.getClass() == clazz) {
            try {
                return ((ArithmeticOperation) clazz.getDeclaredConstructor(ArithmeticExpression.class, ArithmeticExpression.class).newInstance(
                        new Multiply(first, ((ArithmeticOperation) second).getLeft()),
                        new Multiply(first, ((ArithmeticOperation) second).getRight())
                )).toPolynomial();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @Override
    protected Character getSymbol() {
        return '*';
    }
}