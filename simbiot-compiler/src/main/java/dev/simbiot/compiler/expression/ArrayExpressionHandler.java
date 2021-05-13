package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.ArrayExpression;
import dev.simbiot.ast.expression.CallExpression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public class ArrayExpressionHandler implements ExpressionHandler<ArrayExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, ArrayExpression expression) {
        return ctx.resolve(new CallExpression("@array", expression.getElements()));
    }
}
