package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class MetaProperty extends BaseNode implements Expression {
    private final Identifier meta;
    private final Identifier property;

    @JsonCreator
    public MetaProperty(@JsonProperty("meta") Identifier meta,
                        @JsonProperty("property") Identifier property) {
        super("MetaProperty");
        this.meta = meta;
        this.property = property;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Identifier getMeta() {
        return meta;
    }

    public Identifier getProperty() {
        return property;
    }
}
