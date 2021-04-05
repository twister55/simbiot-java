package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class KeyBlock extends Block implements TemplateNode {

    @JsonCreator
    public KeyBlock(@JsonProperty("children") TemplateNode[] children) {
        super("KeyBlock", children);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
