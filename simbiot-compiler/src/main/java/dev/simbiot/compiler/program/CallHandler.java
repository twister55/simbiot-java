package dev.simbiot.compiler.program;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.compiler.CompilerContext;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public interface CallHandler {

    Chunk handle(CompilerContext ctx, Expression[] args);
}
