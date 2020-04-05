package expressions.types;

public interface Expression {

    /**
     * Simplifies expression
     *
     * @return simplified equivalent {@code Expression}
     */
    Expression simplify();
}