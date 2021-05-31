package dev.simbiot.compiler.expression;

import java.util.Objects;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.BinaryExpression;
import dev.simbiot.ast.expression.UnaryExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfEqual;
import dev.simbiot.compiler.bytecode.JumpTarget;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription.InDefinedShape;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.jar.asm.Label;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class BinaryExpressionHandler implements ExpressionHandler<BinaryExpression> {

    /**
     * The {@link Objects#equals(Object, Object)} method.
     */
    private static final InDefinedShape EQUALS = new ForLoadedType(Objects.class)
        .getDeclaredMethods()
        .filter(named("equals"))
        .getOnly();

    @Override
    public StackChunk handle(CompilerContext ctx, BinaryExpression expression) {
        switch (expression.getOperator()) {
            case "==":
            case "===":
                return compare(ctx, expression);

            case "!=":
            case "!==":
                return compareInverted(ctx, expression);

            default:
                throw new UnsupportedNodeException(expression, "binary expression");
        }
    }

    private StackChunk compare(CompilerContext ctx, BinaryExpression expression) {
        StackChunk left = ctx.resolve(expression.getLeft());
        StackChunk right = ctx.resolve(expression.getRight());

        if (left.type().isPrimitive() && left.type().equals(right.type())) {
            return comparePrimitives(left, right);
        }

        return StackChunk.call(EQUALS, ctx.resolve(expression.getLeft(), expression.getRight()));
    }

    private StackChunk compareInverted(CompilerContext ctx, BinaryExpression expression) {
        return ctx.resolve(new UnaryExpression("!", new BinaryExpression(
            expression.getLeft(),
            expression.getRight(),
            invert(expression.getOperator())
        )));
    }

    private String invert(String operator) {
        switch (operator) {
            case "!=":
                return "==";

            case "!==":
                return "===";

            default:
                throw new IllegalArgumentException("Unsupported operator for invert: " + operator);
        }
    }

    private StackChunk comparePrimitives(StackChunk left, StackChunk right) {
        final Label ifLabel = new Label();
        final Label elseLabel = new Label();

        return new StackChunk()
            .append(left)
            .append(right)
            .append(new IfEqual(int.class, ifLabel))
            .append(IntegerConstant.forValue(false))
            .append(new GoTo(elseLabel))
            .append(new JumpTarget(ifLabel))
            .append(IntegerConstant.forValue(true))
            .append(new JumpTarget(elseLabel))
            .as(boolean.class);
    }
}
