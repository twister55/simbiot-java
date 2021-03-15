package dev.simbiot.parser.template;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ElseBlock extends Block {

    @JsonCreator
    public ElseBlock(@JsonProperty("children") TemplateNode[] children) {
        super("ElseBlock", children);
    }

}
