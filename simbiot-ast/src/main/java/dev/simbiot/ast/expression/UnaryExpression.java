package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UnaryExpression extends BaseNode implements Expression {
    private final Expression argument;
    private final String operator; // "-" | "+" | "!" | "~" | "typeof" | "void" | "delete"

    public UnaryExpression(@JsonProperty("argument") Expression argument,
                           @JsonProperty("operator") String operator) {
        super("UnaryExpression");
        this.operator = operator;
        this.argument = argument;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getArgument() {
        return argument;
    }

    public String getOperator() {
        return operator;
    }
}
