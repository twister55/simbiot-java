package dev.simbiot.endorphin.node;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class PartialStatement extends BaseNode {
    private final String id;
    private final ENDAttribute[] params;

    public PartialStatement(String id, ENDAttribute[] params) {
        super("ENDPartialStatement");
        this.id = id;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public ENDAttribute[] getParams() {
        return params;
    }
}
