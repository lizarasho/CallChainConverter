package callchain.parser;

import exceptions.InvalidSyntaxException;
import exceptions.InvalidTypeException;
import expressions.parser.ExpressionParser;
import expressions.types.ArithmeticExpression;
import expressions.types.Expression;
import expressions.types.LogicalExpression;

public class CallChainParser {

    private final static String EXPR_START = "{";
    private final static String EXPR_END = "}";
    private final static String CALLS_SEPARATOR = "%>%";

    private final String callChain;
    private final ExpressionParser exprParser;
    private int curPointer;

    public CallChainParser(String callChain) {
        this.callChain = callChain;
        curPointer = 0;
        exprParser = new ExpressionParser();
    }

    public boolean hasNextCall() {
        return !isEnd();
    }

    public Call getCall() throws InvalidSyntaxException, InvalidTypeException {
        Call filterCall = tryParseCall(CallType.FILTER, LogicalExpression.class);
        Call mapCall = tryParseCall(CallType.MAP, ArithmeticExpression.class);
        Call result = filterCall == null ? mapCall : filterCall;
        if (result == null) {
            throw new InvalidSyntaxException("Illegal operation: expected 'filter' or 'map', found '" + getWord() + "\'");
        }
        if (!isEnd()) {
            if (!callChain.startsWith(CALLS_SEPARATOR, curPointer)) {
                throw new InvalidSyntaxException("Illegal symbol: expected " + CALLS_SEPARATOR + ", found " + callChain.charAt(curPointer));
            }
            curPointer += CALLS_SEPARATOR.length();
            if (isEnd()) {
                throw new InvalidSyntaxException("After " + CALLS_SEPARATOR + " expected <call>, found the end");
            }
        }
        return result;
    }

    private Call tryParseCall(CallType callType, Class<?> operandsType) throws InvalidSyntaxException, InvalidTypeException {
        if (callChain.startsWith(String.valueOf(callType).toLowerCase(), curPointer)) {
            curPointer += String.valueOf(callType).length();
            Expression expr = exprParser.parse(getExpression(callType));
            if (!operandsType.isAssignableFrom(expr.getClass())) {
                throw new InvalidTypeException("Illegal type of operand <" + expr.toString() + "> of function " + String.valueOf(callType).toLowerCase());
            }
            return new Call(callType, expr);
        }
        return null;
    }

    private String getExpression(CallType callType) throws InvalidSyntaxException {
        if (isEnd() || !callChain.startsWith(EXPR_START, curPointer)) {
            throw new InvalidSyntaxException("After <" + String.valueOf(callType).toLowerCase() + "> expression in format '{ <expression> }' was expected");
        }
        int beginIndex = ++curPointer;
        while (curPointer < callChain.length() && !callChain.startsWith(EXPR_END, curPointer)) {
            curPointer++;
        }
        if (curPointer == callChain.length()) {
            throw new InvalidSyntaxException("Expression <" + callChain.substring(beginIndex, curPointer) + "> must end with '" + EXPR_END + '\'');
        }
        return callChain.substring(beginIndex, curPointer++);
    }

    private boolean isEnd() {
        return curPointer >= callChain.length();
    }

    private static String getCallString(Call call) {
        return call.getCallName() + EXPR_START + call.getExpression().toString() + EXPR_END;
    }

    public static String joinCalls(Call first, Call second) {
        return getCallString(first) + CALLS_SEPARATOR + getCallString(second);
    }

    private String getWord() {
        int beginIndex = curPointer;
        while (!isEnd() && Character.isLetter(callChain.charAt(curPointer))) {
            curPointer++;
        }
        return callChain.substring(beginIndex, curPointer);
    }
}
