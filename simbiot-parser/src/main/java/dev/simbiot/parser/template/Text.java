package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Text extends BaseNode implements TemplateNode {
    private final String data;
    private final String raw;

    public Text(@JsonProperty("data") String data,
                @JsonProperty("raw") String raw) {
        super("Text");
        this.data = data;
        this.raw = raw;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getData() {
        return data;
    }

    public String getRaw() {
        return raw;
    }
}
