package expressions.types.operations;

import expressions.types.Expression;

/**
 * Implementation of the {@code Expression} interface representing binary operation.
 * Binary operations must have two operands to create a new value.
 */
public abstract class BinaryOperation implements Expression {

    public abstract Expression getLeft();

    public abstract Expression getRight();

    public abstract void setLeft(Expression left);

    public abstract void setRight(Expression right);
}
