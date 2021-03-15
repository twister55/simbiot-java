package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Head extends Element {

    @JsonCreator
    public Head(@JsonProperty("name") String name,
                @JsonProperty("children") TemplateNode[] children,
                @JsonProperty("attributes") Attribute[] attributes) {
        super("Head", name, children, attributes);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
