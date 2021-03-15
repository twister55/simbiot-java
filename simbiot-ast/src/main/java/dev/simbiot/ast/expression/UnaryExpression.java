package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UnaryExpression extends BaseNode implements Expression {
    // "-" | "+" | "!" | "~" | "typeof" | "void" | "delete"
    private final String operator;
    private final Expression argument;
    private final boolean prefix;

    public UnaryExpression(@JsonProperty("operator") String operator,
                           @JsonProperty("argument") Expression argument,
                           @JsonProperty("prefix") boolean prefix) {
        super("UnaryExpression");
        this.operator = operator;
        this.argument = argument;
        this.prefix = prefix;
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

    public boolean isPrefix() {
        return prefix;
    }
}
