package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Slot extends Element {

    @JsonCreator
    public Slot(@JsonProperty("name") String name,
                @JsonProperty("children") TemplateNode[] children,
                @JsonProperty("attributes") Attribute[] attributes) {
        super("Slot", name, children, attributes);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
