package dev.simbiot.compiler;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.compiler.DispatchResult.Builder;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import static dev.simbiot.compiler.Compiler.CONSTANTS_FIELD_NAME;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Dispatcher {

    public DispatchResult dispatch(ProcessContext ctx, CallExpression call) {
        final Builder builder = new Builder();

        call.getCallee().accept(new ExpressionVisitor() {
            @Override
            public void visit(Identifier expression) {
                final String name = expression.getName();

                switch (name) {
                    case "attr":
                        callGeneric(ctx, builder, "props", "get", call.getArguments());
                        break;

                    case "write":
                        callWrite(ctx, builder, call.getArgument(0), (Literal) call.getArgument(1));
                        break;

                    case "component":
                        renderComponent(ctx, builder, (Literal) call.getArgument(0));
                        break;

                    default:
                        callRuntimeMethod(builder, name, call.getArguments());
                }
            }

            @Override
            public void visit(MemberExpression expression) {
                final Identifier obj = (Identifier) expression.getObject();
                final Identifier method = (Identifier) expression.getProperty();

                callGeneric(ctx, builder, obj.getName(), method.getName(), call.getArguments());
            }
        });

        return builder.build();
    }

    private void callGeneric(ProcessContext ctx, Builder builder, String obj, String method, Expression[] args) {
        builder
            .callee(ctx.get(obj))
            .method(ctx.getType(obj), named(method).and(takesArguments(args.length)))
            .args(args);
    }

    private void callWrite(ProcessContext ctx, Builder builder, Expression value, Literal escape) {
        builder.callee(ctx.get("writer"));

        if (value instanceof Literal && !escape.isTrue()) {
            builder
                .method(ctx.getType("writer"), named("write").and(takesArguments(byte[].class)))
                .arg(new MemberExpression(CONSTANTS_FIELD_NAME, ctx.addConstant(((Literal) value).getString())));
        } else {
            builder
                .method(ctx.getType("writer"), named("write").and(takesArguments(Object.class)))
                .arg(escape.isTrue() ? new CallExpression("escape", value) : value);
        }
    }

    private void renderComponent(ProcessContext ctx, Builder builder, Literal arg) {
        final String name = arg.getString();
        final int idx = ctx.addComponentId(name);

        builder
            .callee(ctx.get("$$components"), IntegerConstant.forValue(idx), ArrayAccess.REFERENCE.load())
            .method(ctx.getType("$$components").getComponentType(), named("render"))
            .args(new Identifier("writer"), new Identifier("props"), new Identifier("slots"));
    }

    private void callRuntimeMethod(Builder builder, String name, Expression[] args) {
        try {
            builder.method(Runtime.class.getMethod(name, Object.class)).args(args);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method " + name + " can not be resolved");
        }
    }
}
