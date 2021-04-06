package dev.simbiot.endorphin.node.expression;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.ArrowFunctionExpression;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDFilter extends BaseNode implements Expression {
    private final Expression object;
    private final ArrowFunctionExpression expression;
    private final boolean multiple;

    public ENDFilter(@JsonProperty("object") Expression object,
                     @JsonProperty("expression") ArrowFunctionExpression expression,
                     @JsonProperty("multiple") boolean multiple) {
        super("ENDCaller");
        this.object = object;
        this.expression = expression;
        this.multiple = multiple;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
