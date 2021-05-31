package dev.simbiot.compiler.expression;

import java.util.HashMap;
import java.util.Map;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ExpressionResolver {
    private final Map<String, ExpressionHandler<Expression>> handlers;
    private final ExpressionHandler<Expression> defaultHandler;

    public ExpressionResolver() {
        this.handlers = new HashMap<>();
        this.defaultHandler = (ctx, expression) -> {
            final Visitor visitor = new Visitor(ctx);
            expression.accept(visitor);
            return visitor.result;
        };
    }

    public StackChunk resolve(CompilerContext ctx, Expression expression) {
        return this.handlers
            .getOrDefault(expression.getType(), this.defaultHandler)
            .handle(ctx, expression);
    }

    @SuppressWarnings("unchecked")
    public <E extends Expression> ExpressionResolver withHandler(String type, ExpressionHandler<E> handler) {
        handlers.put(type, (ExpressionHandler<Expression>) handler);
        return this;
    }

    static class Visitor extends ExpressionVisitor {
        private final CompilerContext ctx;
        private StackChunk result;

        Visitor(CompilerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void visitDefault(Expression expression) {
            result = ctx.resolve(expression);
        }
    }
}
