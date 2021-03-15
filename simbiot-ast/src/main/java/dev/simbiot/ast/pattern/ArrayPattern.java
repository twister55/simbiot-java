package dev.simbiot.ast.pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ArrayPattern extends BaseNode implements Pattern {
    private final Pattern[] elements;

    @JsonCreator
    public ArrayPattern(@JsonProperty("elements") Pattern[] elements) {
        super("ArrayPattern");
        this.elements = elements;
    }

    public Pattern[] getElements() {
        return elements;
    }
}
