package dev.simbiot.endorphin.node.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.endorphin.node.expression.IdentifierWithContext.Context;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class GetterPrefix extends BaseNode {
    private final Context context;

    @JsonCreator
    public GetterPrefix(@JsonProperty("context") Context context) {
        super("ENDGetterPrefix");
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
