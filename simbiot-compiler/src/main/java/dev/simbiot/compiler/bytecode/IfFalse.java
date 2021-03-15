package dev.simbiot.compiler.bytecode;

import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

/**
 * A {@link StackManipulation} which jumps to a destination if the boolean value on the stack is
 * false.
 *
 * <p>Used for if-statements like
 *
 * <pre>{code
 *   if (a) {
 *     ...
 *   }
 *   // destination
 * }</pre>
 *
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class IfFalse implements StackManipulation {
    private final Label label;

    public IfFalse(Label label) {
        this.label = label;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor mv, Context ctx) {
        mv.visitJumpInsn(Opcodes.IFEQ, label);
        return new Size(-1, 0);
    }
}
