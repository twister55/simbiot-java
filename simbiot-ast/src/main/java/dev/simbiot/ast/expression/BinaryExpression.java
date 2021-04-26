package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class BinaryExpression extends BaseNode implements Expression {
    private final Expression left;
    private final Expression right;
    private final String operator; // "===" | "!==" | "<=" | ">" | ">=" | "<<" | ">>" | ">>>" | "+" | "-" | "*" | "/" | "%" | "**" | "|" | "^" | "&" | "in" | "instanceof";

    @JsonCreator
    public BinaryExpression(@JsonProperty("left") Expression left,
                            @JsonProperty("right") Expression right,
                            @JsonProperty("operator") String operator) {
        super("BinaryExpression");
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }
}
