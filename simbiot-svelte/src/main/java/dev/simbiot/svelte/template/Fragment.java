package dev.simbiot.svelte.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Fragment extends Block {

    @JsonCreator
    public Fragment(@JsonProperty("children") TemplateNode[] children) {
        super("Fragment", children);
    }

}
