package dev.simbiot.endorphin.node.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDCaller extends BaseNode implements Expression {
    private final Expression object;
    private final Expression property;
    private final Expression[] arguments;

    public ENDCaller(@JsonProperty("object") Expression object,
                     @JsonProperty("property") Expression property,
                     @JsonProperty("arguments") Expression[] arguments) {
        super("ENDCaller");
        this.object = object;
        this.property = property;
        this.arguments = arguments;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
