package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDDirective extends BaseNode {
    private final String prefix;
    private final String name;
    private final PlainStatement value;

    @JsonCreator
    public ENDDirective(@JsonProperty("prefix") String prefix,
                        @JsonProperty("name") String name,
                        @JsonProperty("value") PlainStatement value) {
        super("ENDDirective");
        this.prefix = prefix;
        this.name = name;
        this.value = value;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public PlainStatement getValue() {
        return value;
    }
}
