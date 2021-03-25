package dev.simbiot.compiler.handler.call;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.handler.ExpressionsHandler;
import dev.simbiot.compiler.handler.HandleResult;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ObjectHandler extends CallHandler {
    private final String obj;
    private final String method;

    public ObjectHandler(ExpressionsHandler parent, String obj, String method) {
        super(parent);
        this.obj = obj;
        this.method = method;
    }

    @Override
    public void handle(CompilerContext ctx, CallExpression call, HandleResult result) {
        result
            .append(ctx.get(obj))
            .append(resolve(ctx, call.getArguments()))
            .invoke(ctx, obj, named(method).and(takesArguments(call.getArgumentsCount())));
    }
}
