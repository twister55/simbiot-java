package dev.simbiot.compiler.expression;

import dev.simbiot.Runtime;
import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.ast.expression.UnaryExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfFalse;
import dev.simbiot.compiler.bytecode.IfTrue;
import dev.simbiot.compiler.bytecode.JumpTarget;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.Label;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class UnaryExpressionHandler implements ExpressionHandler<UnaryExpression> {

    /**
     * The {@link Runtime#is(Object)} method.
     */
    private static final InDefinedShape IS = new ForLoadedType(Runtime.class)
        .getDeclaredMethods()
        .filter(named("is"))
        .getOnly();

    @Override
    public StackChunk handle(CompilerContext ctx, UnaryExpression expression) {
        if (isNegation(expression)) {
            return handleNOT(ctx, expression.getArgument());
        }

        throw new UnsupportedNodeException(expression, "unary expression");
    }

    private boolean isNegation(Expression expression) {
        if (expression instanceof UnaryExpression) {
            final UnaryExpression unary = (UnaryExpression) expression;

            return unary.getOperator().equals("!");
        }

        return false;
    }

    private StackChunk handleNOT(CompilerContext ctx, Expression argument) {
        final Label ifLabel = new Label();
        final Label elseLabel = new Label();

        StackManipulation splitter;
        if (argument instanceof UnaryExpression) {
            final UnaryExpression unary = (UnaryExpression) argument;
            if (!unary.getOperator().equals("!")) {
                throw new UnsupportedNodeException(argument, "unary expression");
            }
            argument = unary.getArgument();
            splitter = new IfFalse(ifLabel);
        } else {
            splitter = new IfTrue(ifLabel);
        }

        return toBoolean(ctx.resolve(argument))
            .append(splitter)
            .append(IntegerConstant.forValue(true))
            .append(new GoTo(elseLabel))
            .append(new JumpTarget(ifLabel))
            .append(IntegerConstant.forValue(false))
            .append(new JumpTarget(elseLabel))
            .as(boolean.class);
    }

    private StackChunk toBoolean(StackChunk value) {
        if (!value.type().represents(boolean.class)) {
            value.append(MethodInvocation.invoke(IS)).as(boolean.class);
        }
        return value;
    }
}
