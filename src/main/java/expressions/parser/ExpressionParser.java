package expressions.parser;

import exceptions.InvalidSyntaxException;
import exceptions.InvalidTypeException;
import expressions.types.LogicalExpression;
import expressions.types.operations.*;
import expressions.types.ArithmeticExpression;
import expressions.types.Expression;
import expressions.types.primitives.Const;
import expressions.types.primitives.Element;

public class ExpressionParser {
    private Tokenizer tokenizer;

    public Expression parse(String expression) throws InvalidSyntaxException, InvalidTypeException {
        tokenizer = new Tokenizer(expression);
        return parseBinaryOperation();
    }

    private Expression parsePrimitive() throws InvalidSyntaxException, InvalidTypeException {
        Expression result = null;
        switch (tokenizer.getNextToken()) {
            case ELEMENT: {
                result = new Element();
                tokenizer.getNextToken();
                break;
            }
            case NUMBER: {
                result = new Const(tokenizer.getValue());
                tokenizer.getNextToken();
                break;
            }
            case OPEN_BRACE: {
                result = parseBinaryOperation();
                tokenizer.getNextToken();
                break;
            }
        }
        return result;
    }

    private Expression parseBinaryOperation() throws InvalidSyntaxException, InvalidTypeException {
        Expression left = parsePrimitive();
        while (true) {
            switch (tokenizer.getCurrentToken()) {
                case ADD: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, ArithmeticExpression.class);
                    left = new Add((ArithmeticExpression) left, (ArithmeticExpression) right);
                    break;
                }
                case SUBTRACT: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, ArithmeticExpression.class);
                    left = new Subtract((ArithmeticExpression) left, (ArithmeticExpression) right);
                    break;
                }
                case MULTIPLY: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, ArithmeticExpression.class);
                    left = new Multiply((ArithmeticExpression) left, (ArithmeticExpression) right);
                    break;
                }
                case GREATER: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, ArithmeticExpression.class);
                    left = new Greater((ArithmeticExpression) left, (ArithmeticExpression) right);
                    break;
                }
                case LESS: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, ArithmeticExpression.class);
                    left = new Less((ArithmeticExpression) left, (ArithmeticExpression) right);
                    break;
                }
                case EQUALS: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, ArithmeticExpression.class);
                    left = new Equals((ArithmeticExpression) left, (ArithmeticExpression) right);
                    break;
                }
                case AND: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, LogicalExpression.class);
                    left = new And((LogicalExpression) left, (LogicalExpression) right);
                    break;
                }
                case OR: {
                    Expression right = parsePrimitive();
                    checkOperandsTypes(left, right, LogicalExpression.class);
                    left = new Or((LogicalExpression) left, (LogicalExpression) right);
                    break;
                }
                default: {
                    return left;
                }
            }
        }
    }

    private void checkOperandsTypes(Expression left, Expression right, Class<?> operandsTypes) throws InvalidTypeException {
        checkOperandType(left, operandsTypes);
        checkOperandType(right, operandsTypes);
    }

    private void checkOperandType(Expression operand, Class<?> type) throws InvalidTypeException {
        if (!type.isAssignableFrom(operand.getClass())) {
            throw new InvalidTypeException("Incorrect type of operand " + operand.toString());
        }
    }
}