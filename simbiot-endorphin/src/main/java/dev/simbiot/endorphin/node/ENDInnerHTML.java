package dev.simbiot.endorphin.node;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.Program;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.statement.ExpressionStatement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ENDInnerHTML extends BaseNode implements ENDNode {
    private final Program value;

    @JsonCreator
    public ENDInnerHTML(@JsonProperty("value") Program value) {
        super("ENDInnerHTML");
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return ((ExpressionStatement) value.getBody()[0]).getExpression();
    }
}
