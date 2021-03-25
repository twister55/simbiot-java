package dev.simbiot.compiler.handler.call;

import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.handler.ExpressionsHandler;
import dev.simbiot.compiler.handler.HandleResult;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import static dev.simbiot.compiler.CompilerContext.COMPONENTS_FIELD_NAME;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ComponentHandler extends CallHandler {

    public ComponentHandler(ExpressionsHandler parent) {
        super(parent);
    }

    @Override
    public void handle(CompilerContext ctx, CallExpression call, HandleResult result) {
        final String name = ((Literal) call.getArgument(0)).getString();
        final int idx = ctx.addComponentId(name);

        result
            .append(ctx.get(COMPONENTS_FIELD_NAME))
            .append(IntegerConstant.forValue(idx))
            .append(ArrayAccess.REFERENCE.load())
            .append(ctx.get("writer"))
            .append(ctx.get("props"))
            .append(ctx.get("slots"))
            .invoke(ctx.getType(COMPONENTS_FIELD_NAME).getComponentType(), named("render"));
    }
}
