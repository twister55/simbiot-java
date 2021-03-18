package dev.simbiot.compiler.bytecode;

import dev.simbiot.ast.expression.Literal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class Constant extends Compound {

    public Constant(Literal literal) {
        super(resolve(literal));
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
