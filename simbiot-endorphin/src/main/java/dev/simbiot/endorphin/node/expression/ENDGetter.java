package dev.simbiot.endorphin.node.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDGetter extends BaseNode implements Expression {
    private final Expression[] path;

    @JsonCreator
    public ENDGetter(@JsonProperty("path") Expression[] path) {
        super("ENDGetter");
        this.path = path;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(new CallExpression("@getter", path));
    }
}
