package dev.simbiot.compiler;

import dev.simbiot.Runtime;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.ExpressionVisitor;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.compiler.DispatchResult.Builder;
import static dev.simbiot.compiler.Compiler.CONSTANTS_FIELD_NAME;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Dispatcher {

    public DispatchResult dispatch(ProcessContext context, CallExpression call) {
        final Builder builder = new Builder();

        call.getCallee().accept(new ExpressionVisitor() {
            @Override
            public void visit(Identifier expression) {
                final String name = expression.getName();

                switch (name) {
                    case "attr":
                        callGeneric(context, builder, "props", "get", call.getArguments());
                        break;

                    case "write":
                        callWrite(context, builder, call.getArgument(0), (Literal) call.getArgument(1));
                        break;

                    default:
                        callRuntimeMethod(builder, name, call.getArguments());
                }
            }

            @Override
            public void visit(MemberExpression expression) {
                final Identifier obj = (Identifier) expression.getObject();
                final Identifier method = (Identifier) expression.getProperty();

                callGeneric(context, builder, obj.getName(), method.getName(), call.getArguments());
            }
        });

        return builder.build();
    }

    private void callGeneric(ProcessContext context, Builder builder, String obj, String method, Expression[] args) {
        builder
            .callee(context.get(obj))
            .method(context.getType(obj), named(method).and(takesArguments(args.length)))
            .args(args);
    }

    private void callWrite(ProcessContext context, Builder builder, Expression value, Literal escape) {
        builder.callee(context.get("writer"));

        if (value instanceof Literal && !escape.isTrue()) {
            builder
                .method(context.getType("writer"), named("write").and(takesArguments(byte[].class)))
                .arg(new MemberExpression(CONSTANTS_FIELD_NAME, context.addConstant(((Literal) value).getString())));
        } else {
            builder
                .method(context.getType("writer"), named("write").and(takesArguments(Object.class)))
                .arg(escape.isTrue() ? new CallExpression("escape", value) : value);
        }
    }

    private void callRuntimeMethod(Builder builder, String name, Expression[] args) {
        try {
            builder.method(Runtime.class.getMethod(name, Object.class)).args(args);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method " + name + " can not be resolved");
        }
    }
}
