package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ClassBody extends BaseNode {
    private final MethodDefinition[] body;

    @JsonCreator
    public ClassBody(@JsonProperty("body") MethodDefinition[] body) {
        super("ClassBody");
        this.body = body;
    }

    public MethodDefinition[] getBody() {
        return body;
    }
}
