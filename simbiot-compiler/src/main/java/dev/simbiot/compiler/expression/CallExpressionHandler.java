package dev.simbiot.compiler.expression;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.ast.expression.MemberExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import static dev.simbiot.compiler.BuiltIn.COMPONENTS_FIELD_NAME;
import static dev.simbiot.compiler.BuiltIn.CONSTANTS_FIELD_NAME;
import static dev.simbiot.compiler.BuiltIn.WRITER;
import static dev.simbiot.compiler.BuiltIn.WRITER_WRITE;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class CallExpressionHandler implements ExpressionHandler<CallExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, CallExpression expression) {
        final Expression callee = expression.getCallee();
        final Expression[] args = expression.getArguments();

        if (callee instanceof Identifier) {
            return dispatch(ctx, (Identifier) callee, args);
        }

        if (callee instanceof MemberExpression) {
            return dispatch(ctx, (MemberExpression) callee, args);
        }

        throw new UnsupportedNodeException(callee, "callee");
    }

    protected StackChunk dispatch(CompilerContext ctx, Identifier callee, Expression[] args) {
        final String name = callee.getName();

        if (name.equals("@write")) {
            Expression value = args[0];
            if (value instanceof Literal) {
                value = new MemberExpression("@" + CONSTANTS_FIELD_NAME, ctx.addConstant((Literal) value));
            }
            return ctx.resolve(new CallExpression(WRITER_WRITE, value));
        }

        if (name.equals("@component")) {
            final int idx = ctx.addComponentId(((Literal) args[0]).getString());
            final MemberExpression newCallee = new MemberExpression(
                new MemberExpression("@" + COMPONENTS_FIELD_NAME, idx),
                new Identifier("render")
            );

            return ctx.resolve(new CallExpression(newCallee, WRITER, args[1]));
        }

        return ctx.call(name, args);
    }

    protected StackChunk dispatch(CompilerContext ctx, MemberExpression callee, Expression[] args) {
        final StackChunk result = ctx.resolve(callee.getObject());
        final Identifier methodId = (Identifier) callee.getProperty();

        return result.invoke(methodId.getName(), ctx.resolve(args));
    }
}
