package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Partial extends BaseNode {
    private final TemplateNode[] body;
    private final ENDAttribute[] params;

    public Partial(@JsonProperty("body") TemplateNode[] body,
                   @JsonProperty("params") ENDAttribute[] params) {
        super("ENDPartial");
        this.body = body;
        this.params = params;
    }

    public TemplateNode[] getBody() {
        return body;
    }

    public ENDAttribute[] getParams() {
        return params;
    }
}
