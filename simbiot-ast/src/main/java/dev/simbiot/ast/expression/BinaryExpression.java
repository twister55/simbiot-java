package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class BinaryExpression extends BaseNode implements Expression {
    // "===" | "!==" | "<=" | ">" | ">=" | "<<" | ">>" | ">>>" | "+" | "-" | "*" | "/" | "%" | "**" | "|" | "^" | "&" | "in" | "instanceof";
    private final String operator;
    private final Expression left;
    private final Expression right;

    @JsonCreator
    public BinaryExpression(@JsonProperty("operator") String operator,
                            @JsonProperty("left") Expression left,
                            @JsonProperty("right") Expression right) {
        super("BinaryExpression");
        this.operator = operator;
        this.left = left;
        this.right = right;
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
