package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CatchBlock extends Block {
    private final boolean skip;

    @JsonCreator
    public CatchBlock(@JsonProperty("children") TemplateNode[] children,
                      @JsonProperty("skip") boolean skip) {
        super("CatchBlock", children);
        this.skip = skip;
    }

    public boolean isSkip() {
        return skip;
    }
}
