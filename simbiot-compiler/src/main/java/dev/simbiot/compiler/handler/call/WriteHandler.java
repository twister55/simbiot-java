package dev.simbiot.compiler.handler.call;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.handler.ExpressionsHandler;
import dev.simbiot.compiler.handler.HandleResult;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import static dev.simbiot.compiler.CompilerContext.CONSTANTS_FIELD_NAME;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class WriteHandler extends CallHandler {

    public WriteHandler(ExpressionsHandler parent) {
        super(parent);
    }

    @Override
    public void handle(CompilerContext ctx, CallExpression call, HandleResult result) {
        final Expression value = call.getArgument(0);
        final Literal escape = (Literal) call.getArgument(1);

        result.append(ctx.get("writer"));

        if (value instanceof Literal && !escape.isTrue()) {
            result
                .append(ctx.get(CONSTANTS_FIELD_NAME))
                .append(IntegerConstant.forValue(ctx.addConstant(((Literal) value).getString())))
                .append(ArrayAccess.REFERENCE.load())
                .invoke(ctx, "writer", named("write").and(takesArguments(byte[].class)));
        } else {
            if (escape.isTrue()) {
                result.append(resolve(ctx, new CallExpression("escape", value)));
            } else {
                result.append(resolve(ctx, value));
            }

            result.invoke(ctx, "writer", named("write").and(takesArguments(Object.class)));
        }
    }
}
