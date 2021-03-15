package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class AwaitExpression extends BaseNode implements Expression {
    private final Expression argument;

    @JsonCreator
    public AwaitExpression(@JsonProperty("argument") Expression argument) {
        super("AwaitExpression");
        this.argument = argument;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getArgument() {
        return argument;
    }
}
