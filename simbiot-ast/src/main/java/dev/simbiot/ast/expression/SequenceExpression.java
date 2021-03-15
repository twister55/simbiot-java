package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SequenceExpression extends BaseNode implements Expression {
    private final Expression[] expressions;

    @JsonCreator
    public SequenceExpression(@JsonProperty("expressions") Expression[] expressions) {
        super("SequenceExpression");
        this.expressions = expressions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression[] getExpressions() {
        return expressions;
    }
}
