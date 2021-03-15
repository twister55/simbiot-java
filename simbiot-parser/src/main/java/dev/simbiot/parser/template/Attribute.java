package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Attribute extends BaseNode {
    private final String name;
    private final TemplateNode[] value;

    @JsonCreator
    public Attribute(@JsonProperty("name") String name,
                     @JsonProperty("value") TemplateNode[] value) {
        super("Attribute");
        this.name = name;
        this.value = value;
    }

    protected Attribute(String type, String name, TemplateNode[] value) {
        super(type);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public TemplateNode[] getValue() {
        return value;
    }
}
