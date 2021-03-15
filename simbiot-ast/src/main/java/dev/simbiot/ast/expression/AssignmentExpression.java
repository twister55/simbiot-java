package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.pattern.Pattern;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class AssignmentExpression extends BaseNode implements Expression {
    // "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "**=" | "<<=" | ">>=" | ">>>=" | "|=" | "^=" | "&="
    private final String operator;
    private final Pattern left;
    private final Expression right;

    public AssignmentExpression(@JsonProperty("operator") String operator,
                                @JsonProperty("left") Pattern left,
                                @JsonProperty("right") Expression right) {
        super("AssignmentExpression");
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getOperator() {
        return operator;
    }

    public Pattern getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }
}
