package dev.simbiot.endorphin.node.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.endorphin.node.expression.IdentifierNode.Context;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDGetterPrefix extends BaseNode implements Expression {
    private final Context context;

    @JsonCreator
    public ENDGetterPrefix(@JsonProperty("context") Context context) {
        super("ENDGetterPrefix");
        this.context = context;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
