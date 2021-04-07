package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDElement extends BaseNode implements ENDNode {
    private final Identifier name;
    private final ENDAttribute[] attributes;
    private final ENDNode[] body;
    private final boolean component;

    public ENDElement(@JsonProperty("name") Identifier name,
                      @JsonProperty("attributes") ENDAttribute[] attributes,
                      @JsonProperty("body") ENDNode[] body,
                      @JsonProperty("component") boolean component) {
        super("ENDElement");
        this.name = name;
        this.attributes = attributes;
        this.body = body;
        this.component = component;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Identifier getName() {
        return name;
    }

    public ENDAttribute[] getAttributes() {
        return attributes;
    }

    public ENDNode[] getBody() {
        return body;
    }

    public boolean isComponent() {
        return component;
    }
}
