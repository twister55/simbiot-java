package dev.simbiot.endorphin.node;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.pattern.Property;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDElement extends BaseNode implements ENDNode {
    private final Identifier name;
    private final ENDAttribute[] attributes;
    private final ENDDirective[] directives;
    private final ENDNode[] body;
    private final boolean component;

    public ENDElement(@JsonProperty("name") Identifier name,
                      @JsonProperty("attributes") ENDAttribute[] attributes,
                      @JsonProperty("directives") ENDDirective[] directives,
                      @JsonProperty("body") ENDNode[] body,
                      @JsonProperty("component") boolean component) {
        super("ENDElement");
        this.name = name;
        this.attributes = attributes;
        this.directives = directives;
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

    public ENDDirective[] getDirectives() {
        return directives;
    }

    public List<Property> getProperties() {
        final List<Property> props = new ArrayList<>();
        for (ENDAttribute attr : getAttributes()) {
            props.add(new Property((Identifier) attr.getName(), attr.getValue()));
        }
        return props;
    }

    public boolean hasChildren() {
        return body.length > 0;
    }

    public ENDNode[] getBody() {
        return body;
    }

    public boolean isComponent() {
        return component;
    }
}
