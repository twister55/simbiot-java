package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.pattern.Property;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ObjectExpression extends BaseNode implements Expression {
    private final Property[] properties;

    @JsonCreator
    public ObjectExpression(@JsonProperty("properties") Property... properties) {
        super("ObjectExpression");
        this.properties = properties;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Property[] getProperties() {
        return properties;
    }
}
