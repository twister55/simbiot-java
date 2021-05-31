package dev.simbiot.compiler.expression;

import java.util.Objects;

import dev.simbiot.ast.expression.ConditionalExpression;
import dev.simbiot.compiler.BuiltIn;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfFalse;
import dev.simbiot.compiler.bytecode.JumpTarget;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.jar.asm.Label;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ConditionalExpressionHandler implements ExpressionHandler<ConditionalExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, ConditionalExpression expression) {
        final Label ifLabel = new Label();
        final Label elseLabel = new Label();

        final StackChunk consequent = ctx.resolve(expression.getConsequent());
        final StackChunk alternate = expression.getAlternate() == null ? StackChunk.NULL : ctx.resolve(expression.getAlternate());

        return ctx.resolve(BuiltIn.toBoolean(expression.getTest()))
            .append(new IfFalse(ifLabel))
            .append(consequent)
            .append(new GoTo(elseLabel))
            .append(new JumpTarget(ifLabel))
            .append(alternate)
            .append(new JumpTarget(elseLabel))
            .as(resolveType(consequent, alternate));
    }

    private Generic resolveType(StackChunk consequent, StackChunk alternate) {
        return Objects.equals(consequent.type(), alternate.type()) ? consequent.type() : Generic.OBJECT;
    }
}
