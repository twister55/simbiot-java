package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDTemplate extends BaseNode implements ENDNode {
    private final ENDNode[] body;

    @JsonCreator
    public ENDTemplate(@JsonProperty("body") ENDNode[] body) {
        super("ENDTemplate");
        this.body = body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public ENDNode[] getBody() {
        return body;
    }
}
