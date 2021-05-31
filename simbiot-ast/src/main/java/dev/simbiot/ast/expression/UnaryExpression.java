package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UnaryExpression extends BaseNode implements Expression {
    private final String operator; // "-" | "+" | "!" | "~" | "typeof" | "void" | "delete"
    private final Expression argument;

    public UnaryExpression(@JsonProperty("operator") String operator,
                           @JsonProperty("argument") Expression argument) {
        super("UnaryExpression");
        this.operator = operator;
        this.argument = argument;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getOperator() {
        return operator;
    }

    public Expression getArgument() {
        return argument;
    }
}
