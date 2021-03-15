package dev.simbiot.ast.pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ObjectPattern extends BaseNode implements Pattern {
    private final Property[] properties;

    @JsonCreator
    public ObjectPattern(@JsonProperty("properties") Property[] properties) {
        super("ObjectPattern");
        this.properties = properties;
    }

    public Property[] getProperties() {
        return properties;
    }
}
