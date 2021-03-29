package dev.simbiot.ast.expression;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CallExpression extends BaseCallExpression {
    public static final Expression[] NO_ARGS = new Expression[0];

    public CallExpression(String calleeObj, String calleeProp) {
        this(new MemberExpression(calleeObj, calleeProp), NO_ARGS, false);
    }

    public CallExpression(String callee, Expression... arguments) {
        this(new Identifier(callee), arguments, false);
    }

    public CallExpression(String callee, List<Expression> arguments) {
        this(new Identifier(callee), arguments.toArray(new Expression[0]), false);
    }

    public CallExpression(Expression callee, Expression... arguments) {
        this(callee, arguments, false);
    }

    @JsonCreator
    public CallExpression(@JsonProperty("callee") Expression callee,
                          @JsonProperty("arguments") Expression[] arguments,
                          @JsonProperty("optional") boolean optional) {
        super("CallExpression", callee, arguments, optional);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
