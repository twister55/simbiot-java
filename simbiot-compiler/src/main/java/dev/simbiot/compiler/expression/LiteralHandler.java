package dev.simbiot.compiler.expression;

import dev.simbiot.ast.expression.Literal;
import dev.simbiot.compiler.CompilerContext;
import dev.simbiot.compiler.bytecode.StackChunk;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class LiteralHandler implements ExpressionHandler<Literal> {
    @Override
    public StackChunk handle(CompilerContext ctx, Literal expression) {
        if (expression.isNull()) {
            return StackChunk.NULL;
        }

        if (expression.isBoolean()) {
            return new StackChunk(boolean.class, IntegerConstant.forValue(expression.isTrue()));
        }

        if (expression.isNumber()) {
            return new StackChunk(int.class, IntegerConstant.forValue(expression.getInt()));
        }

        return new StackChunk(String.class, new TextConstant(expression.getString()));
    }
}
