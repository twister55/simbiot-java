package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ConditionalExpression extends BaseNode implements Expression {
    private final Expression test;
    private final Expression consequent;
    private final Expression alternate;

    @JsonCreator
    public ConditionalExpression(@JsonProperty("test") Expression test,
                                 @JsonProperty("consequent") Expression consequent,
                                 @JsonProperty("alternate") Expression alternate) {
        super("ConditionalExpression");
        this.test = test;
        this.alternate = alternate;
        this.consequent = consequent;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getTest() {
        return test;
    }

    public Expression getConsequent() {
        return consequent;
    }

    public Expression getAlternate() {
        return alternate;
    }
}
