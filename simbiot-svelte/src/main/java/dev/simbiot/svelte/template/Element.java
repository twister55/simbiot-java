package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Element extends Block implements TemplateNode {
    private final String name;
    private final Attribute[] attributes;

    @JsonCreator
    public Element(@JsonProperty("name") String name,
                   @JsonProperty("children") TemplateNode[] children,
                   @JsonProperty("attributes") Attribute[] attributes) {
        this("Element", name, children, attributes);
    }

    protected Element(String type, String name, TemplateNode[] children, Attribute[] attributes) {
        super(type, children);
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }
}
