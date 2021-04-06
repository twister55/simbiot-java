package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Identifier;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDElement extends BaseNode implements TemplateNode {
    private final Identifier name;
    private final ENDAttribute[] attributes;
    private final ENDDirective[] directives;
    private final TemplateNode[] body;
    private final boolean component;
    private final String ref;

    public ENDElement(@JsonProperty("name") Identifier name,
                      @JsonProperty("attributes") ENDAttribute[] attributes,
                      @JsonProperty("directives") ENDDirective[] directives, // TODO remove it ?
                      @JsonProperty("body") TemplateNode[] body,
                      @JsonProperty("component") boolean component,
                      @JsonProperty("ref") String ref) {
        super("ENDElement");
        this.name = name;
        this.attributes = attributes;
        this.directives = directives;
        this.body = body;
        this.component = component;
        this.ref = ref;
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

    public ENDDirective[] getDirectives() {
        return directives;
    }

    public TemplateNode[] getBody() {
        return body;
    }

    public boolean isComponent() {
        return component;
    }

    public String getRef() {
        return ref;
    }
}
