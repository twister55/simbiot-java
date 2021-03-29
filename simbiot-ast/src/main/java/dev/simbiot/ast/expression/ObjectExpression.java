package dev.simbiot.ast.expression;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.pattern.Property;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ObjectExpression extends BaseNode implements Expression, Iterable<Property> {
    private final List<Property> properties;

    @JsonCreator
    public ObjectExpression(@JsonProperty("properties") List<Property> properties) {
        super("ObjectExpression");
        this.properties = properties;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Iterator<Property> iterator() {
        return properties.iterator();
    }
}
