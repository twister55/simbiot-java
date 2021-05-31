package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.Identifier;
import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class IdentifierHandler implements ExpressionHandler<Identifier> {

    @Override
    public StackChunk handle(CompilerContext ctx, Identifier expression) {
        switch (expression.getName()) {
            case "undefined":
                return ctx.resolve(Literal.NULL);

            case "@arg0":
                return ctx.param(0);

            case "@writer":
                return ctx.writer();

            case "@props":
                return ctx.props();

            default:
                return ctx.resolve(expression.getName());
        }
    }
}
