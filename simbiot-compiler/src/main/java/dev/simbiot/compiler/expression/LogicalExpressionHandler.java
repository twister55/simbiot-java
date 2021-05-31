package dev.simbiot.compiler.expression;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.ConditionalExpression;
import dev.simbiot.ast.expression.LogicalExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class LogicalExpressionHandler implements ExpressionHandler<LogicalExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, LogicalExpression expression) {
        switch (expression.getOperator()) {
            case "&&":
                return and(ctx, expression);

            case "||":
                return or(ctx, expression);

            default:
                throw new UnsupportedNodeException(expression, "logical expression");
        }
    }

    private StackChunk and(CompilerContext ctx, LogicalExpression expression) {
        return ctx.resolve(new ConditionalExpression(expression.getLeft(), expression.getRight(), null));
    }

    private StackChunk or(CompilerContext ctx, LogicalExpression expression) {
        return ctx.resolve(
            new ConditionalExpression(expression.getLeft(), expression.getLeft(), expression.getRight())
        );
    }
}
