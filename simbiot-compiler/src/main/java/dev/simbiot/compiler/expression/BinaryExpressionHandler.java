package dev.simbiot.compiler.expression;

import java.util.Objects;

import dev.simbiot.ast.UnsupportedNodeException;
import dev.simbiot.ast.expression.BinaryExpression;
import dev.simbiot.ast.expression.Expression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfEqual;
import dev.simbiot.compiler.bytecode.JumpTarget;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.jar.asm.Label;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class BinaryExpressionHandler implements ExpressionHandler<BinaryExpression> {

    /**
     * The {@link Objects#equals(Object, Object)} method.
     */
    public static final MethodDescription.InDefinedShape EQUALS = new TypeDescription.ForLoadedType(Objects.class)
        .getDeclaredMethods()
        .filter(named("equals"))
        .getOnly();

    @Override
    public StackChunk handle(CompilerContext ctx, BinaryExpression expression) {
        StackChunk left = ctx.resolve(expression.getLeft());
        StackChunk right = ctx.resolve(expression.getRight());

        switch (expression.getOperator()) {
            case "==":
            case "===":
                if (left.type().isPrimitive() && left.type().equals(right.type())) {
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
                        .append(new JumpTarget(elseLabel), boolean.class);
                }

                return StackChunk.call(EQUALS, ctx.resolve(new Expression[] {
                    expression.getLeft(), expression.getRight()
                }));

            case "!=":
            case "!==":
                if (left.type().isPrimitive() && left.type().equals(right.type())) {
                    final Label ifLabel = new Label();
                    final Label elseLabel = new Label();

                    return new StackChunk()
                        .append(left)
                        .append(right)
                        .append(new IfEqual(int.class, ifLabel))
                        .append(IntegerConstant.forValue(true))
                        .append(new GoTo(elseLabel))
                        .append(new JumpTarget(ifLabel))
                        .append(IntegerConstant.forValue(false))
                        .append(new JumpTarget(elseLabel), boolean.class);
                }

                return StackChunk.negation(StackChunk.call(EQUALS, ctx.resolve(new Expression[] {
                    expression.getLeft(), expression.getRight()
                })));

            default:
                throw new UnsupportedNodeException(expression, "binary expression");
        }
    }
}
