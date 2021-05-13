package dev.simbiot.compiler.expression;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.UnaryExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class UnaryExpressionHandler implements ExpressionHandler<UnaryExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, UnaryExpression expression) {
        if ("!".equals(expression.getOperator())) {
            return StackChunk.negation(ctx.resolve(expression.getArgument()));
        }

        throw new UnsupportedNodeException(expression, "unary expression");
    }
}
