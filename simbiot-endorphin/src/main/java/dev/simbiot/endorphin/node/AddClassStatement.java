package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class AddClassStatement extends BaseNode {
    private final PlainStatement[] tokens;

    @JsonCreator
    public AddClassStatement(@JsonProperty("tokens") PlainStatement[] tokens) {
        super("ENDAddClassStatement");
        this.tokens = tokens;
    }

    public PlainStatement[] getTokens() {
        return tokens;
    }
}
