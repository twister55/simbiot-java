package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ArrowFunctionExpression extends BaseFunction implements Expression {
    private final Statement body;
    private final boolean expression;

    @JsonCreator
    public ArrowFunctionExpression(@JsonProperty("body") Statement body,
                                   @JsonProperty("expression") boolean expression,
                                   @JsonProperty("params") Pattern[] params,
                                   @JsonProperty("generator") boolean generator,
                                   @JsonProperty("async") boolean async) {
        super("ArrowFunctionExpression", params, generator, async);
        this.body = body;
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Statement getBody() {
        return body;
    }

    public boolean isExpression() {
        return expression;
    }
}
