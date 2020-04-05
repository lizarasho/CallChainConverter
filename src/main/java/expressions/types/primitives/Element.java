package expressions.types.primitives;

import expressions.types.ArithmeticExpression;
import expressions.types.Expression;
import expressions.types.operations.Multiply;

public class Element implements ArithmeticExpression {
    private int pow;

    public Element(int pow) {
        this.pow = pow;
    }

    public Element() {
        this(1);
    }

    @Override
    public Expression simplify() {
        return this;
    }

    public int getPow() {
        return pow;
    }

    @Override
    public String toString() {
        if (pow == 1) {
            return "element";
        }
        ArithmeticExpression result = new Element();
        for (int i = 1; i < pow; i++) {
            result = new Multiply(result, new Element());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Element element = (Element) obj;
        return pow == element.pow;
    }
}
