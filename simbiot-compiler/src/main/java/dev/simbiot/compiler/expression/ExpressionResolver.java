package dev.simbiot.compiler.expression;

import java.util.HashMap;
import java.util.Map;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class ExpressionResolver {
    private final Map<String, ExpressionHandler<Expression>> handlers;

    public ExpressionResolver() {
        this.handlers = new HashMap<>();
    }

    public StackChunk resolve(CompilerContext ctx, Expression expression) {
        final ExpressionHandler<Expression> handler = handlers.get(expression.getType());
        if (handler == null) {
            throw new UnsupportedNodeException(expression, "expression resolver");
        }
        return handler.handle(ctx, expression);
    }

    @SuppressWarnings("unchecked")
    public <E extends Expression> ExpressionResolver withHandler(String type, ExpressionHandler<E> handler) {
        this.handlers.put(type, (ExpressionHandler<Expression>) handler);
        return this;
    }
}
