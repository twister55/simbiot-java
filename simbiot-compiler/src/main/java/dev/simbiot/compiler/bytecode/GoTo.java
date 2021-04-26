package dev.simbiot.compiler.bytecode;

import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

/**
 * A {@link StackManipulation} which jumps unconditionally to a destination.
 *
 * <p>Used at the end of the block of loops to jump back to the loop condition.
 *
 * <pre>{code
 *   while (&#47;* destination *&#47; a != b) {
 *     ...
 *     // goto destination
 *   }
 * }</pre>
 *
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
public class GoTo implements StackManipulation {
    private final Label destination;

    public GoTo(Label destination) {
        this.destination = destination;
    }

    @Override
    public boolean isValid() {
        return destination != null;
    }

    @Override
    public Size apply(MethodVisitor mv, Context ctx) {
        mv.visitJumpInsn(Opcodes.GOTO, destination);
        return new Size(0, 0);
    }
}
