package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.ConditionalExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.GoTo;
import dev.simbiot.compiler.bytecode.IfFalse;
import dev.simbiot.compiler.bytecode.JumpTarget;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.jar.asm.Label;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class ConditionalExpressionHandler implements ExpressionHandler<ConditionalExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, ConditionalExpression expression) {
        final Label ifLabel = new Label();
        final Label elseLabel = new Label();

        return StackChunk.condition(ctx.resolve(expression.getTest()))
            .append(new IfFalse(ifLabel))
            .append(ctx.resolve(expression.getConsequent()))
            .append(new GoTo(elseLabel))
            .append(new JumpTarget(ifLabel))
            .append(ctx.resolve(expression.getAlternate()))
            .append(new JumpTarget(elseLabel));
    }
}
