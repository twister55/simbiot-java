package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface ExpressionHandler<E extends Expression> {

    StackChunk handle(CompilerContext ctx, E expression);

}
