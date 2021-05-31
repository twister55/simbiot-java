package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.ArrowFunctionExpression;
import dev.simbiot.ast.expression.FunctionExpression;
import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ArrowFunctionExpressionHandler implements ExpressionHandler<ArrowFunctionExpression> {

    @Override
    public StackChunk handle(CompilerContext ctx, ArrowFunctionExpression expression) {
        return ctx.resolve(new FunctionExpression(
            new Identifier("fn" + System.nanoTime()),
            expression.getBody(),
            expression.getParams(),
            expression.isGenerator(),
            expression.isGenerator()
        ));
    }
}
