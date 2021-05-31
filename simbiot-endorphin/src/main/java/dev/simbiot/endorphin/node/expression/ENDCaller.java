package dev.simbiot.endorphin.node.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Literal;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDCaller extends BaseNode implements Expression {
    private final GetterPrefix object;
    private final Literal property;
    private final Expression[] arguments;

    public ENDCaller(@JsonProperty("object") GetterPrefix object,
                     @JsonProperty("property") Literal property,
                     @JsonProperty("arguments") Expression[] arguments) {
        super("ENDCaller");
        this.object = object;
        this.property = property;
        this.arguments = arguments;
    }

    @Override
    public void accept(Visitor visitor) {
        throw new UnsupportedNodeException(this, "visitor");
    }
}
