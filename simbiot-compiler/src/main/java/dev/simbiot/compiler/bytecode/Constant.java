package dev.simbiot.compiler.bytecode;

import dev.simbiot.ast.expression.Literal;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.jar.asm.MethodVisitor;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Constant implements StackManipulation {
    private final StackManipulation delegate;

    public Constant(Literal literal) {
        this.delegate = resolve(literal);
    }

    @Override
    public boolean isValid() {
        return delegate.isValid();
    }

    @Override
    public Size apply(MethodVisitor mv, Context context) {
        return delegate.apply(mv, context);
    }

    private static StackManipulation resolve(Literal literal) {
        if (literal.isNull()) {
            return NullConstant.INSTANCE;
        }

        if (literal.isBoolean()) {
            return IntegerConstant.forValue(literal.isTrue());
        }

        if (literal.isNumber()) {
            return IntegerConstant.forValue(literal.getInt());
        }

        return new TextConstant(literal.getString());
    }
}
