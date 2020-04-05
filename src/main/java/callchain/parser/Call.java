package callchain.parser;

import expressions.types.Expression;

public class Call {
    private final CallType callType;
    private final Expression expression;

    public Call(CallType callType, Expression expression) {
        this.callType = callType;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public CallType getCallType() {
        return callType;
    }

    public String getCallName() {
        return String.valueOf(callType).toLowerCase();
    }
}
