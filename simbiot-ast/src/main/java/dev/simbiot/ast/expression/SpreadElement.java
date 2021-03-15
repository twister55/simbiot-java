package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class SpreadElement extends BaseNode {
    private final Expression argument;

    @JsonCreator
    public SpreadElement(@JsonProperty("argument") Expression argument) {
        super("SpreadElement");
        this.argument = argument;
    }

    public Expression getArgument() {
        return argument;
    }
}
