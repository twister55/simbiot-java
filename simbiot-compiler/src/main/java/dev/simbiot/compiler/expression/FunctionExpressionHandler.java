package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.FunctionExpression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.FunctionContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import static dev.simbiot.compiler.BuiltIn.PROPS;
import static dev.simbiot.compiler.BuiltIn.WRITER;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class FunctionExpressionHandler implements ExpressionHandler<FunctionExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, FunctionExpression expression) {
        final FunctionContext context = ctx.createInlineContext();
        final TypeDescription type = context.compile(expression);

        return new StackChunk(
            type.asGenericType(),
            new StackManipulation.Compound(
                TypeCreation.of(type),
                Duplication.SINGLE,
                ctx.resolve(WRITER),
                ctx.resolve(PROPS),
                context.vars(ctx::resolve),
                ctx.resolve(new Identifier("@components")),
                MethodInvocation.invoke(type.getDeclaredMethods().filter(isConstructor()).getOnly())
            )
        );
    }
}
