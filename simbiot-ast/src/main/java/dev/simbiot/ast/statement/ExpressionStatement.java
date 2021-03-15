package dev.simbiot.ast.statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.BaseNode;
import dev.simbiot.ast.expression.Expression;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExpressionStatement extends BaseNode implements Statement {
    private final Expression expression;

    @JsonCreator
    public ExpressionStatement(@JsonProperty("expression") Expression expression) {
        super("ExpressionStatement");
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
