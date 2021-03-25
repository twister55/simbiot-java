package dev.simbiot.compiler.handler.call;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.handler.ExpressionsHandler;
import dev.simbiot.compiler.handler.HandleResult;
import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class StaticHandler extends CallHandler {
    private final ForLoadedMethod method;

    public StaticHandler(ExpressionsHandler parent, Class<?> type, String methodName) {
        super(parent);
        this.method = forName(type, methodName);
    }

    @Override
    public void handle(CompilerContext ctx, CallExpression call, HandleResult result) {
        result.append(resolve(ctx, call.getArguments())).invoke(method);
    }

    private static ForLoadedMethod forName(Class<?> type, String name) {
        try {
            return new ForLoadedMethod(type.getMethod(name, Object.class));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
