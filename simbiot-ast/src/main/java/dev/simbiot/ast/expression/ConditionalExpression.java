package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ConditionalExpression extends BaseNode implements Expression {
    private final Expression test;
    private final Expression alternate;
    private final Expression consequent;

    @JsonCreator
    public ConditionalExpression(@JsonProperty("test") Expression test,
                                 @JsonProperty("alternate") Expression alternate,
                                 @JsonProperty("consequent") Expression consequent) {
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

    public Expression getAlternate() {
        return alternate;
    }

    public Expression getConsequent() {
        return consequent;
    }
}
