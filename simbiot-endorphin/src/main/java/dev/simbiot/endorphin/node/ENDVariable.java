package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDVariable extends BaseNode {
    private final String name;
    private final PlainStatement value;

    @JsonCreator
    public ENDVariable(@JsonProperty("name") String name,
                       @JsonProperty("value") PlainStatement value) {
        super("ENDVariable");
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public PlainStatement getValue() {
        return value;
    }
}
