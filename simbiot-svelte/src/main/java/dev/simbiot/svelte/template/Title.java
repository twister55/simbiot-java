package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Title extends Element {

    @JsonCreator
    public Title(@JsonProperty("name") String name,
                 @JsonProperty("children") TemplateNode[] children,
                 @JsonProperty("attributes") Attribute[] attributes) {
        super("Title", name, children, attributes);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
