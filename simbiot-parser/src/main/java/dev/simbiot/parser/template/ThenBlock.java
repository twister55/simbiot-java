package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ThenBlock extends Block {
    private final boolean skip;

    @JsonCreator
    public ThenBlock(@JsonProperty("children") TemplateNode[] children,
                     @JsonProperty("skip") boolean skip) {
        super("ThenBlock", children);
        this.skip = skip;
    }

    public boolean isSkip() {
        return skip;
    }

}
