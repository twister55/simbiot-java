package dev.simbiot.ast.pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class RestElement extends BaseNode implements Pattern {
    private final Pattern argument;

    @JsonCreator
    public RestElement(@JsonProperty("argument") Pattern argument) {
        super("RestElement");
        this.argument = argument;
    }

    public Pattern getArgument() {
        return argument;
    }
}
