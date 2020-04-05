package expressions.parser;

import exceptions.InvalidSyntaxException;

import java.util.*;
import java.util.function.Function;

import static expressions.parser.Token.*;

class Tokenizer {

    private final String expression;

    private Token curToken;
    private int curPointer;
    private int braceBalance;
    private int value;
    private Deque<Integer> operandsNumber;

    private static final String ELEMENT = "element";
    private static final Map<Character, Token> binaryOperations = new HashMap<>();

    static {
        binaryOperations.put('+', ADD);
        binaryOperations.put('-', SUBTRACT);
        binaryOperations.put('*', MULTIPLY);
        binaryOperations.put('>', GREATER);
        binaryOperations.put('<', LESS);
        binaryOperations.put('=', EQUALS);
        binaryOperations.put('&', AND);
        binaryOperations.put('|', OR);
    }

    private static final Set<Token> operations = EnumSet.of(
            ADD,
            SUBTRACT,
            MULTIPLY,
            GREATER,
            LESS,
            EQUALS,
            AND,
            OR
    );


    Tokenizer(String expression) {
        this.expression = expression;
        curPointer = braceBalance = 0;
        curToken = BEGIN;
        operandsNumber = new ArrayDeque<>();
    }

    Token getCurrentToken() {
        return curToken;
    }

    Token getNextToken() throws InvalidSyntaxException {
        readToken();
        return getCurrentToken();
    }

    int getValue() {
        return value;
    }

    private boolean isBinaryExpression() {
        if (ELEMENT.equals(expression)) {
            return false;
        }
        try {
            Integer.parseInt(expression);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isEnd(int position) {
        return position > expression.length() - 1;
    }

    private boolean needOperation() {
        return (curToken == Token.NUMBER || curToken == Token.ELEMENT || curToken == Token.CLOSE_BRACE);
    }

    private void checkOperation() throws InvalidSyntaxException {
        if (needOperation()) {
            throw new InvalidSyntaxException("operation was expected");
        }
    }

    private void checkPrimitive() throws InvalidSyntaxException {
        if (curToken == BEGIN || curToken == OPEN_BRACE || operations.contains(curToken)) {
            throw new InvalidSyntaxException("Before position " + curPointer + " in expression <" + expression +
                    "> primitive was expected");
        }
    }

    private String readByFunction(Function<Character, Boolean> function) {
        int beginIndex = curPointer;
        while (curPointer < expression.length() && function.apply(expression.charAt(curPointer))) {
            curPointer++;
        }
        return expression.substring(beginIndex, curPointer--);
    }

    private void processNumber(boolean minus) throws InvalidSyntaxException {
        String number = readByFunction(Character::isDigit);
        if (minus) {
            number = '-' + number;
        }
        try {
            value = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new InvalidSyntaxException("Integer overflow: number " + (minus ? '-' : "") + number + " is too large");
        }
        curToken = NUMBER;
    }

    private void updateOperandStack() throws InvalidSyntaxException {
        if (!isBinaryExpression()) {
            return;
        }
        if (operandsNumber.isEmpty()) {
            throw new InvalidSyntaxException("Invalid braces arrangement: every binary expression of expression + <"
                    + expression + "> must be in braces");
        }
        operandsNumber.push(operandsNumber.pop() + 1);
    }

    private void readToken() throws InvalidSyntaxException {
        if (isEnd(curPointer)) {
            checkPrimitive();
            if (braceBalance != 0) {
                throw new InvalidSyntaxException("Invalid braces arrangement: not enough close braces in the end of expression " + expression);
            }
            return;
        }
        char symbol = expression.charAt(curPointer);
        switch (symbol) {
            case '-': {
                if (needOperation()) {
                    curToken = SUBTRACT;
                } else {
                    curPointer++;
                    if (isEnd(curPointer) || !Character.isDigit(expression.charAt(curPointer))) {
                        throw new InvalidSyntaxException("After unary minus at position " + curPointer +
                                " in expression <" + expression + "> number was expected, found <" + expression.charAt(curPointer) + ">");
                    }
                    processNumber(true);
                    updateOperandStack();
                }
                break;
            }
            case '(': {
                checkOperation();
                braceBalance++;
                curToken = OPEN_BRACE;
                if (!operandsNumber.isEmpty()) {
                    operandsNumber.push(operandsNumber.pop() + 1);
                }
                operandsNumber.push(0);
                break;
            }
            case ')': {
                checkPrimitive();
                braceBalance--;
                if (braceBalance < 0) {
                    throw new InvalidSyntaxException("Invalid braces arrangement: extra close brace at position " +
                            curPointer + " of expression <" + expression + ">");
                }
                curToken = CLOSE_BRACE;
                int operands = operandsNumber.pop();
                if ((operandsNumber.isEmpty() && !isEnd(curPointer + 1)) || operands != 2) {
                    throw new InvalidSyntaxException("Invalid braces arrangement: every binary expression of expression + <"
                            + expression + "> must be in braces");
                }
                break;
            }
            default: {
                if (binaryOperations.containsKey(symbol)) {
                    checkPrimitive();
                    curToken = binaryOperations.get(symbol);
                    break;
                }
                if (Character.isDigit(symbol)) {
                    checkOperation();
                    processNumber(false);
                    updateOperandStack();
                    break;
                }
                String word = readByFunction(Character::isLetter);
                if (!ELEMENT.equals(word)) {
                    throw new InvalidSyntaxException("Illegal symbol \'" + symbol + "\' in expression " + expression);
                }
                checkOperation();
                curToken = Token.ELEMENT;
                updateOperandStack();
                break;
            }
        }
        curPointer++;
    }
}
