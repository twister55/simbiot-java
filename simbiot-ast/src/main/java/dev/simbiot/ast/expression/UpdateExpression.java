package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UpdateExpression extends BaseNode implements Expression {
    private final Identifier argument;
    private final Operator operator;
    private final boolean prefix;

    public static UpdateExpression increment(Identifier argument) {
        return new UpdateExpression(argument, Operator.INCREMENT, false);
    }

    public static UpdateExpression decrement(Identifier argument) {
        return new UpdateExpression(argument, Operator.DECREMENT, false);
    }

    @JsonCreator
    public UpdateExpression(@JsonProperty("argument") Identifier argument,
                            @JsonProperty("operator") Operator operator,
                            @JsonProperty("prefix") boolean prefix) {
        super("UpdateExpression");
        this.argument = argument;
        this.operator = operator;
        this.prefix = prefix;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Identifier getArgument() {
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
