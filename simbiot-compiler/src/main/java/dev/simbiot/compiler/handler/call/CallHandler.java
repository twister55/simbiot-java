package dev.simbiot.compiler.handler.call;

import org.jetbrains.annotations.Nullable;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.handler.HandleResult;
import dev.simbiot.compiler.handler.Handler;
import dev.simbiot.compiler.handler.ExpressionsHandler;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public abstract class CallHandler implements Handler<CallExpression> {
    private final ExpressionsHandler parent;

    public CallHandler(ExpressionsHandler parent) {
        this.parent = parent;
    }

    protected HandleResult resolve(CompilerContext ctx, Expression[] expressions) {
        HandleResult result = new HandleResult();
        for (Expression arg : expressions) {
            parent.handle(ctx, arg, result);
        }
        return result;
    }

    protected HandleResult resolve(CompilerContext ctx, @Nullable Expression expression) {
        HandleResult result = new HandleResult();
        if (expression != null) {
            parent.handle(ctx, expression, result);
        }
        return result;
    }
}
