import callchain.parser.Call;
import callchain.parser.CallChainParser;
import callchain.parser.CallType;
import exceptions.InvalidSyntaxException;
import exceptions.InvalidTypeException;
import expressions.types.ArithmeticExpression;
import expressions.types.Expression;
import expressions.types.LogicalExpression;
import expressions.types.operations.And;
import expressions.types.primitives.Bool;
import expressions.types.primitives.Element;
import expressions.types.operations.BinaryOperation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class to convert <call-chain> into the equivalent simplified form <filter-call> “%>%” <map-call>
 */
public class CallChainConverter {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                System.out.println(convert(line));
            } catch (InvalidSyntaxException e) {
                System.out.println("SYSTEM ERROR: " + e.getMessage());
            } catch (InvalidTypeException e) {
                System.out.println("TYPE ERROR: " + e.getMessage());
            }
        }
    }


    /**
     * Converts input {@link String} {@code callChain} representing any sequence of <filter-call> and <map-call>
     * into the equivalent simplified {@link String} in format <filter-call> “%>%” <map-call>.
     *
     * @param callChain {@link String} representing <call-chain> to convert
     * @return {@link String} representing equivalent simplified chain in specified format
     * @throws InvalidSyntaxException if {@code callChain} is in invalid format
     * @throws InvalidTypeException   if there is call with wrong argument type or argument type
     *                                can not be inferred because of wrong typifying in expression
     */
    public static String convert(String callChain) throws InvalidSyntaxException, InvalidTypeException {
        CallChainParser parser = new CallChainParser(callChain);

        ArithmeticExpression curMapElement = new Element();
        LogicalExpression curFilterExpr = new Bool(true);

        while (parser.hasNextCall()) {
            Call call = parser.getCall();
            Expression expr = replaceElement(call.getExpression(), curMapElement);
            switch (call.getCallType()) {
                case FILTER: {
                    curFilterExpr = new And(curFilterExpr, (LogicalExpression) expr);
                    break;
                }
                case MAP: {
                    curMapElement = (ArithmeticExpression) expr;
                    break;
                }
            }
        }
        Expression resultFilterExpr = curFilterExpr.simplify();
        Expression resultMapExpr = new Element();

        if (!(resultFilterExpr instanceof Bool) || ((Bool) resultFilterExpr).isTrue()) {
            resultMapExpr = curMapElement.simplify();
        }

        return CallChainParser.joinCalls(new Call(CallType.FILTER, resultFilterExpr), new Call(CallType.MAP, resultMapExpr));
    }

    private static Expression replaceElement(Expression expression, ArithmeticExpression curElement) {
        if (expression instanceof Element) {
            return curElement;
        }
        if (expression instanceof BinaryOperation) {
            BinaryOperation operation = (BinaryOperation) expression;
            operation.setLeft(replaceElement(operation.getLeft(), curElement));
            operation.setRight(replaceElement(operation.getRight(), curElement));
            return operation;
        }
        return expression;
    }
}
