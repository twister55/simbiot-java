package dev.simbiot.compiler.program;

import dev.simbiot.ast.expression.Expression;
import dev.simbiot.compiler.CompilerContext;

/**
 * @author <a href="mailto:vadim.eliseev@corp.mail.ru">Vadim Eliseev</a>
 */
public interface CallHandler {

    Chunk handle(CompilerContext ctx, Expression[] args);
}
