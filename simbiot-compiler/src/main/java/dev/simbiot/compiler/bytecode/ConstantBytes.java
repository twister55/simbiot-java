package dev.simbiot.compiler.bytecode;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation.Compound;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class ConstantBytes extends Compound {
    private static final MethodDescription GET_BYTES;

    static {
        try {
            GET_BYTES = new MethodDescription.ForLoadedMethod(String.class.getMethod("getBytes"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ConstantBytes(String text) {
        super(new TextConstant(text), MethodInvocation.invoke(GET_BYTES));
    }
}
