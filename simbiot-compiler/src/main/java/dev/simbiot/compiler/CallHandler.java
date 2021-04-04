package dev.simbiot.compiler;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.Chunk;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface CallHandler {

    Chunk handle(CompilerContext ctx, Expression[] args);
}
