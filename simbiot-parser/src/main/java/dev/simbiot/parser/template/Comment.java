package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Comment extends BaseNode implements TemplateNode {
    private final String data;

    @JsonCreator
    public Comment(@JsonProperty("data") String data) {
        super("Comment");
        this.data = data;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getData() {
        return data;
    }

}
