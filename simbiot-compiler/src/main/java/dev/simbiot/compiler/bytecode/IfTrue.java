package dev.simbiot.compiler.bytecode;

import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

/**
 * A {@link StackManipulation} which jumps to a destination if the boolean value on the stack is
 * true.
 *
 * <p>Used for if-statements like
 *
 * <pre>{code
 *   if (!a) {
 *     ...
 *   }
 *   // destination
 * }</pre>
 *
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public final class IfTrue implements StackManipulation {
    private final Label destination;

    public IfTrue(Label destination) {
        this.destination = destination;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Context implementationContext) {
        methodVisitor.visitJumpInsn(Opcodes.IFNE, destination);
        return StackSize.SINGLE.toDecreasingSize();
    }
}
