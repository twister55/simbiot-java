package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDPartial extends BaseNode implements ENDNode {
    private final ENDNode[] body;
    private final ENDAttribute[] params;

    @JsonCreator
    public ENDPartial(@JsonProperty("body") ENDNode[] body,
                      @JsonProperty("params") ENDAttribute[] params) {
        super("ENDPartial");
        this.body = body;
        this.params = params;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public ENDNode[] getBody() {
        return body;
    }

    public ENDAttribute[] getParams() {
        return params;
    }
}
