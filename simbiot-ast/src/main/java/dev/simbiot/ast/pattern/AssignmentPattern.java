package dev.simbiot.ast.pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class AssignmentPattern extends BaseNode implements Pattern {
    private final Pattern left;
    private final Expression right;

    @JsonCreator
    public AssignmentPattern(@JsonProperty("left") Pattern left,
                             @JsonProperty("right") Expression right) {
        super("AssignmentPattern");
        this.left = left;
        this.right = right;
    }

    public Pattern getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }
}
