package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDPartialStatement extends BaseNode implements ENDNode {
    private final String id;
    private final ENDAttribute[] params;

    @JsonCreator
    public ENDPartialStatement(@JsonProperty("id") String id,
                               @JsonProperty("params") ENDAttribute[] params) {
        super("ENDPartialStatement");
        this.id = id;
        this.params = params;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getId() {
        return id;
    }

    public ENDAttribute[] getParams() {
        return params;
    }
}
