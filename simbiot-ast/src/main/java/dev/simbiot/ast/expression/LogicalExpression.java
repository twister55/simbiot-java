package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class LogicalExpression extends BaseNode implements Expression {
    private final Operator operator;
    private final Expression left;
    private final Expression right;

    @JsonCreator
    public LogicalExpression(@JsonProperty("operator") Operator operator,
                             @JsonProperty("left") Expression left,
                             @JsonProperty("right") Expression right) {
        super("LogicalExpression");
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public enum Operator {
        AND("&&"),
        OR("||");

        private final String value;

        Operator(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
