package dev.simbiot.ast.expression;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.simbiot.ast.Node;
import dev.simbiot.ast.pattern.Pattern;
import dev.simbiot.ast.statement.BlockStatement;
import dev.simbiot.ast.statement.ReturnStatement;
import dev.simbiot.ast.statement.Statement;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ArrowFunctionExpression extends BaseFunction implements Expression {
    private final Node body;
    private final boolean expression;

    public ArrowFunctionExpression(Expression statement) {
        this(statement, NO_ARGS);
    }

    public ArrowFunctionExpression(Expression statement, Pattern[] params) {
        this(statement, true, params, false, false);
    }

    public ArrowFunctionExpression(Statement statement) {
        this(statement, NO_ARGS);
    }

    public ArrowFunctionExpression(Statement body, Pattern[] params) {
        this(body, false, params, false, false);
    }

    @JsonCreator
    public ArrowFunctionExpression(@JsonProperty("body") Node body,
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

    public BlockStatement getBody() {
        if (expression) {
            return new BlockStatement(new ReturnStatement((Expression) body));
        }

        return body instanceof BlockStatement ? (BlockStatement) body : new BlockStatement((Statement) body);
    }

    public boolean isExpression() {
        return expression;
    }
}
