package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ArrayExpression extends BaseNode implements Expression {
    private final Expression[] elements; // TODO Array<Expression | SpreadElement> ?

    @JsonCreator
    public ArrayExpression(@JsonProperty("elements") Expression[] elements) {
        super("ArrayExpression");
        this.elements = elements;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression[] getElements() {
        return elements;
    }
}
