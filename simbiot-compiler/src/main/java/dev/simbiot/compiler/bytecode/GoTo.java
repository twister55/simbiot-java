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
    private final Label label;

    public GoTo(Label label) {
        this.label = label;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Size apply(MethodVisitor mv, Context ctx) {
        mv.visitJumpInsn(Opcodes.GOTO, label);
        return new Size(0, 0);
    }
}
