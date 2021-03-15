package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UpdateExpression extends BaseNode implements Expression {
    private final Operator operator;
    private final Expression argument;
    private final boolean prefix;

    public static UpdateExpression increment(Expression argument) {
        return new UpdateExpression(Operator.INCREMENT, argument, false);
    }

    public static UpdateExpression decrement(Expression argument) {
        return new UpdateExpression(Operator.DECREMENT, argument, false);
    }

    @JsonCreator
    public UpdateExpression(@JsonProperty("operator") Operator operator,
                            @JsonProperty("argument") Expression argument,
                            @JsonProperty("prefix") boolean prefix) {
        super("UpdateExpression");
        this.operator = operator;
        this.argument = argument;
        this.prefix = prefix;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getArgument() {
        return argument;
    }

    public Operator getOperator() {
        return operator;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public enum Operator {
        INCREMENT("++"),
        DECREMENT("--");

        private final String value;

        Operator(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
