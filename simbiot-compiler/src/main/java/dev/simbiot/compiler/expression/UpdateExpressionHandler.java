package dev.simbiot.compiler.expression;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.UpdateExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import static net.bytebuddy.implementation.bytecode.member.MethodVariableAccess.INTEGER;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UpdateExpressionHandler implements ExpressionHandler<UpdateExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, UpdateExpression expression) {
        final int offset = ctx.offset(expression.getArgument().getName());

        switch (expression.getOperator()) {
            case INCREMENT:
                return new StackChunk(int.class, INTEGER.increment(offset, 1));

            case DECREMENT:
                return new StackChunk(int.class, INTEGER.increment(offset, -1));

            default:
                throw new UnsupportedNodeException(expression, "update expression");
        }
    }
}
