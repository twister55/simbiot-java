package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CallExpression extends BaseCallExpression {

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
